package poker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.*;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.dto.game.GameStateDTO;
import poker.dto.game.StartGameRequest;
import poker.game.GameRegistry;
import poker.game.playeraction.PlayerAction;
import poker.service.GameActionService;
import poker.service.GameService;
import poker.util.Util;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@Log4j2
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameRegistry gameRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameActionService gameActionService;

    @GetMapping
    public List<GameDTO> getGames() {
        log.info("Get games list");
        var gamesList = gameService.getGamesList();
        log.info("Return games list");
        return gamesList;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(@RequestBody CreateGameRequest createGameRequest) {
        var creatorPlayerId = Util.getPlayerDetailsFronCtx()
            .getPlayer()
            .getId();
        log.info("Create game request {} from player id {}", createGameRequest, creatorPlayerId);

        var game = gameService.createGame(creatorPlayerId, createGameRequest);

        if (game != null) {
            gameRegistry.registerGame(game);
            log.info("Created game id {}", game.getId());
            return ResponseEntity.ok().build();
        }

        log.error("Creation game error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Game creation error. Please, try again later");
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGame(@PathVariable Long id) {
        long userId = Util.getPlayerDetailsFronCtx().getUser().getId();
        log.info("Remove game request, game id {}, user id {}", id, userId);
        gameService.removeGame(id);
        log.info("Game id {} successfully removed", id);
    }

    @PostMapping("/startGame")
    public ResponseEntity<Void> startGame(@RequestBody StartGameRequest startGameRequest) {
        long gameId = startGameRequest.gameId();
        long playerId = startGameRequest.playerId();
        var playerDetails = Util.getPlayerDetailsFronCtx();
        log.info("Start game id {} request by player id {}", gameId, playerId);
        log.debug("PlayerDetails: {}", playerDetails);

//        TODO: validate

        gameActionService.handlePlayerAction(gameId, playerDetails, PlayerAction.START_GAME);

//        TODO: return game state from engine
        var gameStateDTO = gameActionService.getCurrentState(gameId);
        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        simpMessagingTemplate.convertAndSend("/topic/gameTable/" + gameId, message);

        log.info("Start game {} response {}", gameId, gameStateDTO);
        return ResponseEntity.ok().build();
    }
}
