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
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.service.GameTableService;
import poker.service.WebSocketPlayerSessionService;

@Controller
@Log4j2
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameService gameService;
    private final GameTableService gameTableService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketGameController(WebSocketPlayerSessionService webSocketPlayerSessionService, GameService gameServicer,
                                   GameTableService gameTableService, SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.gameService = gameServicer;
        this.gameTableService = gameTableService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @SubscribeMapping("/gameTable/{id}")
    public GameDTO subscribe(@DestinationVariable("id") Long gameId,
                             @AuthenticationPrincipal Authentication authentication,
                             StompHeaderAccessor stompHeaderAccessor) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        log.debug("SUBSCRIBE player details {}", playerDetails);
        Long userId = playerDetails.getUser().getId();

        log.info("SUBSCRIBE user id {}, game id {}", userId, gameId);
        log.debug("SUBSCRIBE authentication {}", authentication);

        var game = gameService.joinPlayerToGame(gameId, playerDetails);

        Long playerId = playerDetails.getPlayer().getId();
        String sessionID = stompHeaderAccessor.getSessionId();

        webSocketPlayerSessionService.addSession(userId, playerId, game.getId(), sessionID);
        log.info("SUBSCRIBE user id {} joined to game id {}", userId, gameId);

        log.debug("SUBSCRIBE player details {}", playerDetails);

        var gameTables = gameTableService.getAllPlayersSitDownAtTable(game.getId());

        var gameDTO = GameConverter.toDTO(game, gameTables.size());
        log.info("SUBSCRIBE {}", gameDTO);

//        notify other players about some player joined to the game
        String destination = "/topic/gameTable/" + gameId;
        Message<GameDTO> message = new GenericMessage<>(gameDTO);
        simpMessagingTemplate.convertAndSend(destination, message);

//        return initial game state to subscribed user
        return gameDTO;
    }

    @MessageMapping("/table/{id}")
    @SendTo("/topic/gameTable/{id}")
    public Message<GameDTO> handleMessage(@DestinationVariable("id") Long gameId,
                                          @Payload String newGameName,
                                          @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long userId = playerDetails.getUser().getId();
        log.info("SEND user id {}, game id {}, new game name {}", userId, gameId, newGameName);

        var game = gameService.updateGameName(gameId, newGameName);
        var gameTables = gameTableService.getAllPlayersSitDownAtTable(game.getId());
        var gameDTO = GameConverter.toDTO(game, gameTables.size());
        log.info("SEND {}", gameDTO);

        Message<GameDTO> outboundMessage = new GenericMessage<>(gameDTO);
        log.info("SEND {}", outboundMessage);

//        return game state
        return outboundMessage;
    }
}
