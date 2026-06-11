package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import poker.dto.game.GameDTO;
import poker.dto.game.GameStateDTO;
import poker.game.playeraction.PlayerAction;
import poker.model.PlayerDetails;
import poker.service.GameEngineService;
import poker.service.GameService;
import poker.service.WebSocketPlayerSessionService;

import java.util.Collections;

@Controller
@Log4j2
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameService gameService;
    private final GameEngineService gameEngineService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketGameController(WebSocketPlayerSessionService webSocketPlayerSessionService, GameService gameServicer,
                                   GameEngineService gameEngineService, SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.gameService = gameServicer;
        this.gameEngineService = gameEngineService;
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

//        TODO: if player eliminated, then he can not join the game

        log.info("SUBSCRIBE user id {}, player id {}, game id {}", userId, playerId, gameId);
        log.debug("SUBSCRIBE authentication {}", authentication);

        var gameStateDTO = gameEngineService.handlePlayerAction(gameId, playerDetails, PlayerAction.JOIN_GAME);

        if (gameStateDTO != null) {
            String sessionID = stompHeaderAccessor.getSessionId();
            webSocketPlayerSessionService.addSession(userId, playerId, gameId, sessionID);

            log.info("SUBSCRIBE {}", gameStateDTO);

//            notify other players about some player joined to the game
            String destination = "/topic/gameTable/" + gameId;
            Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
            simpMessagingTemplate.convertAndSend(destination, message);

            return gameStateDTO;
        }

//        TODO: return last valid game state
        gameStateDTO = GameStateDTO
            .builder()
            .gameDTO(GameDTO.builder().build())
            .playerDTOList(Collections.emptyList())
            .build();

        log.info("GameStateDTO: {}", gameStateDTO);
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

    @MessageMapping("/table/{id}")
    @SendTo("/topic/gameTable/{id}")
    public Message<String> handlePlayerAction(@DestinationVariable("id") Long gameId,
                                              @Payload String newGameName,
                                              @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long userId = playerDetails.getUser().getId();
        log.info("SEND user id {}, game id {}, new game name {}", userId, gameId, newGameName);

//        TODO: take action as a method parameter
//        GameState gameState = gameManagerService.handleAction(gameId, -1L, PlayerAction.START_GAME);
//        log.info("Game state {}", gameState);

        Message<String> outboundMessage = new GenericMessage<>("OK");
        log.info("SEND {}", outboundMessage);

//        return game state
        return outboundMessage;
    }
}
