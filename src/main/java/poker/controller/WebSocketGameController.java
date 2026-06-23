package poker.controller;

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
import poker.service.GameEngineService;
import poker.service.GameService;
import poker.service.WebSocketPlayerSessionService;

@Controller
@Log4j2
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameService gameService;
    private final GameEngineService gameEngineService;
    private final GameActionService gameActionService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketGameController(WebSocketPlayerSessionService webSocketPlayerSessionService, GameService gameServicer,
                                   GameEngineService gameEngineService, GameActionService gameActionService,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.gameService = gameServicer;
        this.gameEngineService = gameEngineService;
        this.gameActionService = gameActionService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

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

    @MessageMapping("/table/{id}/startGame")
    public void startGame(@DestinationVariable("id") Long gameId,
                          @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();
        log.info("Start game id {}, user id {}, player id {}", gameId, userId, playerId);

        var game = gameService.getGameById(gameId);
        if (!game.getCreatorPlayerId().equals(playerId)) {
            log.info("Player id {} is trying to start the game {} without permission", playerId, gameId);
            return;
        }

        GameStateDTO gameStateDTO = gameEngineService.handlePlayerAction(gameId, playerDetails, PlayerAction.START_GAME);
        if (gameStateDTO != null) {
            log.info("Start game, gameStateDTO {}", gameStateDTO);

            Message<GameStateDTO> outboundMessage = new GenericMessage<>(gameStateDTO);
            log.info("Start game, message {}", outboundMessage);

            simpMessagingTemplate.convertAndSend("/topic/gameTable/" + gameId, outboundMessage);
        } // else handler error on client (frontend) side
    }

    @MessageMapping("/table/{id}/action")
//    @SendTo("/topic/gameTable/{id}")
    public void handlePlayerAction(@DestinationVariable("id") Long gameId,
                                   @Payload PlayerActionRequest playerActionRequest,
                                   @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        var playerAction = PlayerAction.fromActionName(playerActionRequest.action());
        long playerId = playerDetails.getPlayer().getId();
        log.info("Action {} from player id {} in game id {}", playerAction.getActionName(), playerId, gameId);

        // todo: validate

        GameStateDTO gameStateDTO = gameActionService.handlePlayerAction(gameId, playerDetails, playerAction);

//        TODO: notify other players about change game state
        Message<String> outboundMessage = new GenericMessage<>("OK");
        log.info("Action {} from player id {} in game {} handled", playerAction.getActionName(), playerId, gameId);
        log.info("GameStateDTO: {}", gameStateDTO);
        log.info("Return response: {}", outboundMessage);
    }
}
