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
import poker.dto.player.PlayerConverter;
import poker.dto.player.PlayerDTO;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.dto.game.GameStateDTO;
import poker.game.GameState;
import poker.game.PlayerAction;
import poker.model.GameStatus;
import poker.model.GameTable;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.service.*;

import java.util.List;

@Controller
@Log4j2
public class WebSocketGameController {
    private final WebSocketPlayerSessionService webSocketPlayerSessionService;
    private final GameService gameService;
    private final GameTableService gameTableService;
    private final PlayerService playerService;
    private final GameManagerService gameManagerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketGameController(WebSocketPlayerSessionService webSocketPlayerSessionService, GameService gameServicer,
                                   GameTableService gameTableService, PlayerService playerService,
                                   GameManagerService gameManagerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketPlayerSessionService = webSocketPlayerSessionService;
        this.gameService = gameServicer;
        this.gameTableService = gameTableService;
        this.playerService = playerService;
        this.gameManagerService = gameManagerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @SubscribeMapping("/gameTable/{id}")
    public GameStateDTO subscribe(@DestinationVariable("id") Long gameId,
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

        gameManagerService.registerGame(game.getId());

        var gameTables = gameTableService.getGameTablesByGameId(game.getId());
        var playerIdsList = gameTables.stream()
            .map(GameTable::getPlayerId)
            .toList();
        log.debug("PlayerIdsList {}", playerIdsList);

        List<Player> players = playerService.getPlayersByIds(playerIdsList);
        players.forEach(log::debug);

        List<PlayerDTO> playerDTOList = PlayerConverter.toListDTO(players);

        var gameDTO = GameConverter.toDTO(game, gameTables.size());
        var gameStateDTO = GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();

        log.info("SUBSCRIBE {}", gameStateDTO);

//        notify other players about some player joined to the game
        String destination = "/topic/gameTable/" + gameId;
        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        simpMessagingTemplate.convertAndSend(destination, message);

//        return initial game state to subscribed user
        return gameStateDTO;
    }

    @MessageMapping("/table/{id}/startGame")
    @SendTo("/topic/gameTable/{id}")
    public Message<GameStateDTO> startGame(@DestinationVariable("id") Long gameId,
                                           @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long userId = playerDetails.getUser().getId();
        log.info("Start game game id {}, user id {}", gameId, userId);

        Long playerId = playerDetails.getPlayer().getId();
        GameState gameState = gameManagerService.handleAction(gameId, playerId, PlayerAction.STUB);
        log.info("Start game id {}, game state {}", gameId, gameState);

        gameService.updateGameState(gameId, GameStatus.START);

        var game = gameService.getGameById(gameId);
        var gameTables = gameTableService.getGameTablesByGameId(gameId);

        var playerIdsList = gameTables.stream()
            .map(GameTable::getPlayerId)
            .toList();
        List<Player> players = playerService.getPlayersByIds(playerIdsList);
        List<PlayerDTO> playerDTOList = PlayerConverter.toListDTO(players);

        GameDTO gameDTO = GameConverter.toDTO(game, gameTables.size());
        GameStateDTO gameStateDTO = GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();

        log.info("Start game, gameStateDTO {}", gameStateDTO);

        Message<GameStateDTO> outboundMessage = new GenericMessage<>(gameStateDTO);
        log.info("Start game, message {}", outboundMessage);

        return outboundMessage;
    }

    @MessageMapping("/table/{id}")
    @SendTo("/topic/gameTable/{id}")
    public Message<String> handlePlayerAction(@DestinationVariable("id") Long gameId,
                                              @Payload String newGameName,
                                              @AuthenticationPrincipal Authentication authentication) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long userId = playerDetails.getUser().getId();
        log.info("SEND user id {}, game id {}, new game name {}", userId, gameId, newGameName);

//        TODO: implement strategy to handle various actions from players
        GameState gameState = gameManagerService.handleAction(gameId, -1L, PlayerAction.STUB);
        log.info("Game state {}", gameState);

        Message<String> outboundMessage = new GenericMessage<>("OK");
        log.info("SEND {}", outboundMessage);

//        return game state
        return outboundMessage;
    }
}
