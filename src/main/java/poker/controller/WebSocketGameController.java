package poker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import poker.dto.PlayerActionRequest;
import poker.dto.game.GameStateDTO;
import poker.game.playeraction.PlayerAction;
import poker.model.PlayerDetails;
import poker.service.GameActionService;
import poker.service.WebSocketPlayerSessionService;

@Controller
@Log4j2
@RequiredArgsConstructor
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameActionService gameActionService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/gameTable/{id}")
    public GameStateDTO subscribe(@DestinationVariable("id") Long gameId,
                                  @AuthenticationPrincipal Authentication authentication,
                                  StompHeaderAccessor stompHeaderAccessor) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        log.debug("SUBSCRIBE player details {}", playerDetails);
        long userId = playerDetails.getUser().getId();
        long playerId = playerDetails.getPlayer().getId();

        log.info("SUBSCRIBE user id {}, player id {}, game id {}", userId, playerId, gameId);
        log.debug("SUBSCRIBE authentication {}", authentication);

        String sessionID = stompHeaderAccessor.getSessionId();
        webSocketPlayerSessionService.addSession(userId, playerId, gameId, sessionID);

        var gameStateDTO = gameActionService.getCurrentState(gameId);
        log.info("SUBSCRIBE {}", gameStateDTO);

//        notify other players about some player joined to the game
        String destination = "/topic/gameTable/" + gameId;
        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        simpMessagingTemplate.convertAndSend(destination, message);

        return gameStateDTO;
    }

    @MessageMapping("/table/{id}/action")
//    @SendTo("/topic/gameTable/{id}")
    public void handlePlayerAction(@DestinationVariable("id") Long gameId,
                                   @Payload PlayerActionRequest playerActionRequest,
                                   @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        var playerAction = PlayerAction.fromActionName(playerActionRequest.actionName());
        long playerId = playerDetails.getPlayer().getId();
        log.info("Action {} from player id {} in game id {}", playerAction.getActionName(), playerId, gameId);

        // todo: validate

        gameActionService.handlePlayerAction(gameId, playerDetails, playerAction);
//        TODO: return game state from engine
        GameStateDTO gameStateDTO = gameActionService.getCurrentState(gameId);

//        TODO: return game state to notify other players about change game state
        Message<String> outboundMessage = new GenericMessage<>("OK");
        log.info("Action {} from player id {} in game {} handled", playerAction.getActionName(), playerId, gameId);
        log.info("GameStateDTO: {}", gameStateDTO);
        log.info("Return response: {}", outboundMessage);
    }
}
