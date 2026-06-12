package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.game.GameRegistry;
import poker.service.GameService;
import poker.util.Util;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Log4j2
public class GamesController {
    private final GameService gameService;
    private final GameRegistry gameRegistry;

    public GamesController(GameService gameService, GameRegistry gameRegistry) {
        this.gameService = gameService;
        this.gameRegistry = gameRegistry;
    }

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
}
