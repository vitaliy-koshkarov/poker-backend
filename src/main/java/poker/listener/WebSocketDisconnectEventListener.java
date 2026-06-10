package poker.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import poker.dto.game.GameStateDTO;
import poker.game.playeraction.PlayerAction;
import poker.model.*;
import poker.service.*;

@Component
@Log4j2
public class WebSocketDisconnectEventListener {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameEngineService gameEngineService;

    public WebSocketDisconnectEventListener(WebSocketPlayerSessionService webSocketPlayerSessionService,
                                            PlayerService playerService,
                                            SimpMessagingTemplate simpMessagingTemplate,
                                            GameEngineService gameEngineService) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameEngineService = gameEngineService;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        log.info("Disconnect event");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("Accessor: {}", accessor);
//        String accessorSessionId = accessor.getSessionId();
//        log.info("Accessor session id {}", accessorSessionId);

        var authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        if (authentication == null) {
            return;
        }

        var playerDetails = (PlayerDetails) authentication.getPrincipal();

        String sessionId = event.getSessionId();
        log.info("Disconnect session id {}", sessionId);

        Long gameId = disconnectPlayer(playerDetails, sessionId);
        Long playerId = playerDetails.getPlayer().getId();

        var gameStateDTO = gameEngineService.handleAction(gameId, playerId, PlayerAction.DISCONNECT);

        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        log.debug("Message {}", message);
        String destination = "/topic/gameTable/" + gameId;

//        Notify other players about some player disconnected and update game state
        simpMessagingTemplate.convertAndSend(destination, message);

        log.info("Player {} disconnected from {}", playerId, destination);
    }

    /**
     * Removes player's session, updates {@link Player#getStatus()} and removes from {@link GameTable}
     * @param playerDetails object with user and player data
     * @param sessionId web socket session of the player
     * @return {@link Game#getId()}
     */
    private Long disconnectPlayer(PlayerDetails playerDetails, String sessionId) {
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();

        var playerSession = webSocketPlayerSessionService.removeSession(sessionId);
        log.info("Disconnect event, player session {}", playerSession);

        Player player = playerDetails.getPlayer();
        player.setStatus(PlayerStatus.NOT_IN_GAME.getStatus());
//        TODO: 1. Check the reason of disconnection
//              2. Set chips to 0 after N minutes
        playerService.updatePlayer(player);

        Long gameId = playerSession.gameId();

        log.info("Disconnect event user id {}, player id {} left game id {}", userId, playerId, gameId);
        log.debug("Disconnect event player details {}", playerDetails);

        return gameId;
    }
}
