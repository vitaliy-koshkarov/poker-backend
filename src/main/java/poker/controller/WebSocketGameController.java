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
import poker.core.player.PlayerActionData;
import poker.dto.PlayerActionDataConverter;
import poker.dto.PlayerActionRequest;
import poker.dto.game.GameDTO;
import poker.core.player.PlayerAction;
import poker.model.PlayerDetails;
import poker.service.*;

@Controller
@Log4j2
@RequiredArgsConstructor
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final PlayerActionHandlerService playerActionHandlerService;
    private final GameStateResponseGenerator gameStateResponseGenerator;
    private final WebSocketGameStateBroadcaster webSocketGameStateBroadcaster;
    private final ValidationService validationService;

    @SubscribeMapping("/gameTable/{id}")
    public GameDTO subscribe(@DestinationVariable("id") Long gameId,
                             @AuthenticationPrincipal Authentication authentication,
                             StompHeaderAccessor stompHeaderAccessor) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        log.debug("Subscribe player details {}", playerDetails);
        long userId = playerDetails.getUser().getId();
        long playerId = playerDetails.getPlayer().getId();

        log.info("Subscribe user id {}, player id {}, game id {}", userId, playerId, gameId);
        log.debug("Subscribe authentication {}", authentication);

        if (!validationService.isGameExists(gameId)) {
            log.error("Game id {} does not exists, player id {}", gameId, playerId);
            return GameDTO.builder().build();
        }

        String sessionID = stompHeaderAccessor.getSessionId();
        webSocketPlayerSessionService.addSession(userId, playerId, gameId, sessionID);

        var gameDTO = gameStateResponseGenerator.generateResponse(gameId);
        log.info("Player id {} subscribed", playerId);

        return gameDTO;
    }

    @MessageMapping("/table/{id}/action")
    public void handlePlayerAction(@DestinationVariable("id") Long gameId,
                                   @Payload PlayerActionRequest playerActionRequest,
                                   @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        var playerAction = PlayerAction.fromActionName(playerActionRequest.actionName());
        long playerId = playerDetails.getPlayer().getId();
        log.info("Action {} player id {} game id {}", playerAction.getActionName(), playerId, gameId);

        if (!validationService.isGameExists(gameId)) {
            log.error("Game id {} does not exists, player id {}", gameId, playerId);
            return;
        }

        if (!validationService.isPlayerActionValid(gameId, playerDetails, playerAction, playerActionRequest.playerBet())) {
            log.error("Invalid action {} game id {} player id {}", playerAction, gameId, playerId);
//            todo: Let the player know that he is doing something wrong?
            return;
        }

        PlayerActionData pad = PlayerActionDataConverter.convert(gameId, playerActionRequest, playerDetails, playerAction);
        playerActionHandlerService.handle(pad);

        var gameDTO = gameStateResponseGenerator.generateResponse(gameId);
        webSocketGameStateBroadcaster.broadcast(gameDTO, playerAction);
        log.info("Handled {} player id {} game id {}", playerAction.getActionName(), playerId, gameId);
    }
}
