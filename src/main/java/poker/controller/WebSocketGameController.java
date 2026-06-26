package poker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import poker.dto.PlayerActionRequest;
import poker.dto.game.GameStateDTO;
import poker.game.PlayerAction;
import poker.model.PlayerDetails;
import poker.service.WebSocketGameStateBroadcaster;
import poker.service.GameStateResponseGenerator;
import poker.service.PlayerActionHandlerService;
import poker.service.WebSocketPlayerSessionService;

@Controller
@Log4j2
@RequiredArgsConstructor
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final PlayerActionHandlerService playerActionHandlerService;
    private final GameStateResponseGenerator gameStateResponseGenerator;
    private final WebSocketGameStateBroadcaster webSocketGameStateBroadcaster;

    @SubscribeMapping("/gameTable/{id}")
    public GameStateDTO subscribe(@DestinationVariable("id") Long gameId,
                                  @AuthenticationPrincipal Authentication authentication,
                                  StompHeaderAccessor stompHeaderAccessor) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        log.debug("Subscribe player details {}", playerDetails);
        long userId = playerDetails.getUser().getId();
        long playerId = playerDetails.getPlayer().getId();

        log.info("Subscribe user id {}, player id {}, game id {}", userId, playerId, gameId);
        log.debug("Subscribe authentication {}", authentication);

        String sessionID = stompHeaderAccessor.getSessionId();
        webSocketPlayerSessionService.addSession(userId, playerId, gameId, sessionID);

        var gameStateDTO = gameStateResponseGenerator.generateResponse(gameId);
        log.info("Player id {} subscribed", playerId);

        return gameStateDTO;
    }

    @MessageMapping("/table/{id}/action")
    public void handlePlayerAction(@DestinationVariable("id") Long gameId,
                                   @Payload PlayerActionRequest playerActionRequest,
                                   @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        var playerAction = PlayerAction.fromActionName(playerActionRequest.actionName());
        long playerId = playerDetails.getPlayer().getId();
        log.info("Action {} from player id {} in game id {}", playerAction.getActionName(), playerId, gameId);

        // todo: validate

        playerActionHandlerService.handlePlayerAction(gameId, playerDetails, playerAction);

        var gameStateDTO = gameStateResponseGenerator.generateResponse(gameId);
        webSocketGameStateBroadcaster.broadcast(gameStateDTO, playerAction);
        log.info("Handled {} from player id {} in game id {}", playerAction.getActionName(), playerId, gameId);
    }
}
