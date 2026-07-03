package poker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import poker.core.engine.GameEngineRegistry;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.dto.PlayerActionDataConverter;
import poker.dto.game.GameDTO;
import poker.core.game.GameStatus;
import poker.core.player.PlayerAction;
import poker.model.*;
import poker.service.*;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketDisconnectEventListener {
    private final GameEngineRegistry gameEngineRegistry;
    private final PlayerActionHandlerService playerActionHandlerService;
    private final GameStateResponseGenerator gameStateResponseGenerator;
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final WebSocketGameStateBroadcaster webSocketGameStateBroadcaster;

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

//        TODO: think how to handle accidental disconnects

        boolean isJoinedPlayerDisconnect = isJoinedPlayerDisconnect(gameId, playerId);

        PlayerActionData pad = PlayerActionDataConverter.convert(gameId, playerDetails, PlayerAction.DISCONNECT);
        playerActionHandlerService.handle(pad);

        webSocketPlayerSessionService.removeSession(sessionId);
        log.info("Disconnect player id {} session id {}", playerId, sessionId);

        GameDTO gameDTO = gameStateResponseGenerator.generateResponse(gameId);

        if (!GameStatus.WAITING_FOR_PLAYERS.getShortName().equals(gameDTO.status()) || isJoinedPlayerDisconnect) {
            webSocketGameStateBroadcaster.broadcast(gameDTO, PlayerAction.DISCONNECT);
        }

        log.info("Player id {} {} from game id {}", playerId, pad.getPlayerAction(), gameId);
    }

    private boolean isJoinedPlayerDisconnect(long gameId, long playerId) {
        for (GamePlayer gamePlayer : gameEngineRegistry.getGameEngine(gameId).getTable().getPlayers()) {
            if (gamePlayer.getId() == playerId) {
                return true;
            }
        }
        return false;
    }
}
