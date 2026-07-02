package poker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.core.player.PlayerActionData;
import poker.dto.PlayerActionDataConverter;
import poker.dto.game.*;
import poker.core.engine.GameEngineRegistry;
import poker.core.player.PlayerAction;
import poker.service.WebSocketGameStateBroadcaster;
import poker.service.GameStateResponseGenerator;
import poker.service.PlayerActionHandlerService;
import poker.service.GameService;
import poker.util.Util;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@Log4j2
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameEngineRegistry gameEngineRegistry;
    private final PlayerActionHandlerService playerActionHandlerService;
    private final GameStateResponseGenerator gameStateResponseGenerator;
    private final WebSocketGameStateBroadcaster webSocketGameStateBroadcaster;

    @GetMapping
    public List<GameDTO> getGames() {
        log.info("Get games list");
        return gameStateResponseGenerator.getGamesListForLobby();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(@RequestBody CreateGameRequest createGameRequest) {
        var creatorPlayerId = Util.getPlayerDetailsFronCtx()
            .getPlayer()
            .getId();
        log.info("Create game request {} from player id {}", createGameRequest, creatorPlayerId);

        var game = gameService.createGame(creatorPlayerId, createGameRequest);

        if (game != null) {
            gameEngineRegistry.registerGame(game);
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

        boolean isSuccess = gameService.removeGame(id);
        if (isSuccess) {
            gameEngineRegistry.removeGame(id);
        }

        log.info("Game id {} successfully removed", id);
    }

    @PostMapping("/startGame")
    public ResponseEntity<Void> startGame(@RequestBody StartGameRequest startGameRequest) {
        long gameId = startGameRequest.gameId();
        long playerId = startGameRequest.playerId();
        var playerDetails = Util.getPlayerDetailsFronCtx();
        log.info("Start game id {} request, player id {}", gameId, playerId);
        log.debug("PlayerDetails: {}", playerDetails);

//        TODO: validate

        PlayerActionData pad = PlayerActionDataConverter.convert(gameId, playerDetails, PlayerAction.START_GAME);
        playerActionHandlerService.handlePlayerAction(pad);

        GameStateDTO gameStateDTO = gameStateResponseGenerator.generateResponse(gameId);
        webSocketGameStateBroadcaster.broadcast(gameStateDTO, PlayerAction.START_GAME);

        log.info("Start game {} response OK", gameId);
        return ResponseEntity.ok().build();
    }
}
