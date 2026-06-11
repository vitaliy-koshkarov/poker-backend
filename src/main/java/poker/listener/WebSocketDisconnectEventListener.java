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
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameEngineService gameEngineService;

    public WebSocketDisconnectEventListener(WebSocketPlayerSessionService webSocketPlayerSessionService,
                                            SimpMessagingTemplate simpMessagingTemplate,
                                            GameEngineService gameEngineService) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
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
        var playerGameSession = webSocketPlayerSessionService.getPlayerSession(sessionId);
        long gameId = playerGameSession.gameId();
        long playerId = playerDetails.getPlayer().getId();

        var gameStateDTO = gameEngineService.handlePlayerAction(gameId, playerDetails, PlayerAction.DISCONNECT);

        webSocketPlayerSessionService.removeSession(sessionId);
        log.info("Disconnect player id {} session id {}", playerId, sessionId);

        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        log.debug("Message {}", message);
        String destination = "/topic/gameTable/" + gameId;

//        Notify other players about some player disconnected and update game state
        simpMessagingTemplate.convertAndSend(destination, message);

        log.info("Player id {} disconnected from {}", playerId, destination);
    }
}
