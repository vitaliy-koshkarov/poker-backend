package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.service.GameTableService;
import poker.service.PlayerSessionService;

@Controller
@Log4j2
public class WebSocketGameController {
    private final PlayerSessionService playerSessionService;
    private final GameService gameService;
    private final GameTableService gameTableService;

    public WebSocketGameController(PlayerSessionService playerSessionService, GameService gameServicer, GameTableService gameTableService) {
        this.playerSessionService = playerSessionService;
        this.gameService = gameServicer;
        this.gameTableService = gameTableService;
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
        var gameTables = gameTableService.getGameTablesById(gameId);

        Long playerId = playerDetails.getPlayer().getId();
        String sessionID = stompHeaderAccessor.getSessionId();

        playerSessionService.addSession(userId, playerId, gameId, sessionID);
        log.info("SUBSCRIBE user id {} joined to game id {}", userId, gameId);

        log.debug("SUBSCRIBE player details {}", playerDetails);

        var gameDTO = GameConverter.toDTO(game, gameTables.size());
        log.info("SUBSCRIBE {}", gameDTO);

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
        var gameTables = gameTableService.getGameTablesById(game.getId());
        var gameDTO = GameConverter.toDTO(game, gameTables.size());
        log.info("SEND {}", gameDTO);

        Message<GameDTO> outboundMessage = new GenericMessage<>(gameDTO);
        log.info("SEND {}", outboundMessage);

        return outboundMessage;
    }
}
