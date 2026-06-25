package poker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import poker.dto.game.GameStateDTO;
import poker.game.PlayerAction;
import poker.model.*;
import poker.service.*;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketDisconnectEventListener {
    private final PlayerActionHandlerService playerActionHandlerService;
    private final GameStateResponseGenerator gameStateResponseGenerator;
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameStateBroadcaster gameStateBroadcaster;

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

        playerActionHandlerService.handlePlayerAction(gameId, playerDetails, PlayerAction.DISCONNECT);

        GameStateDTO gameStateDTO = gameStateResponseGenerator.generateResponse(gameId);

        webSocketPlayerSessionService.removeSession(sessionId);
        log.info("Disconnect player id {} session id {}", playerId, sessionId);

//        TODO: do not broadcast, if the game not started yet
        gameStateBroadcaster.broadcast(gameStateDTO, PlayerAction.DISCONNECT);
        log.info("Player id {} disconnected from game id {}", playerId, gameId);
    }
}
