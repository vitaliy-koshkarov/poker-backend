package poker.listener;

import common.PlayerStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.service.GameTableService;
import poker.service.PlayerService;
import poker.service.WebSocketPlayerSessionService;

@Component
@Log4j2
public class WebSocketEventListener {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameService gameService;
    private final GameTableService gameTableService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketEventListener(WebSocketPlayerSessionService webSocketPlayerSessionService, GameService gameService,
                                  GameTableService gameTableService, PlayerService playerService,
                                  SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.gameService = gameService;
        this.gameTableService = gameTableService;
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
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

        var game = gameService.getGameById(gameId);
        var gameTables = gameTableService.getAllPlayersSitDownAtTable(gameId);

        var gameDTO = GameConverter.toDTO(game, gameTables.size());

        Message<GameDTO> message = new GenericMessage<>(gameDTO);
        String destination = "/topic/gameTable/" + gameId;

        log.debug("Message {}", message);
        log.info("Sending message to {} about player id {} disconnect",
            destination, playerDetails.getPlayer().getId());

//        Notify other players about some player disconnected and update game state
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    private Long disconnectPlayer(PlayerDetails playerDetails, String sessionId) {
//        TODO: return game state?
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();

//        TODO: 1 player - 1 game (table) yet
        var playerSession = webSocketPlayerSessionService.removeSession(sessionId);
        log.info("Disconnect event, player session {}", playerSession);

        Long gameId = playerSession.gameId();
        gameTableService.removePlayerFromGameTable(userId, playerId, gameId);

        playerDetails.getPlayer().setStatus(PlayerStatus.NOT_IN_GAME);

        playerService.updatePlayerStatus(playerId, PlayerStatus.NOT_IN_GAME);
        log.info("Player id {} status updated to {}", playerId, PlayerStatus.NOT_IN_GAME);

        log.info("Disconnect event user id {}, player id {} left game id {}", userId, playerId, gameId);
        log.debug("Disconnect event player details {}", playerDetails);

        return gameId;
    }
}
