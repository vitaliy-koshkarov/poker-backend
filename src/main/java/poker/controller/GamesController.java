package poker.controller;

import lombok.extern.log4j.Log4j2;
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
        log.info("Get list of games");
        return gameService.getGamesList();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestBody CreateGameRequest createGameRequest) {
        Long creatorPlayerId = Util.getPlayerDetailsFronCtx()
            .getPlayer()
            .getId();
        log.info("Create game {} from player {}", createGameRequest, creatorPlayerId);

        Long gameId = gameService.createGame(creatorPlayerId, createGameRequest);

        gameRegistry.registerGame(gameId, createGameRequest.buyIn());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGame(@PathVariable Long id) {
        long userId = Util.getPlayerDetailsFronCtx().getUser().getId();
        log.info("Remove game request, game id {}, user id {}", id, userId);
        gameService.removeGame(id);
    }
}
