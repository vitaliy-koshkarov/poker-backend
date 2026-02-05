package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.service.GameService;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Log4j2
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameDTO> getGames() {
        log.info("Get list of games");
        return gameService.getGamesList();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestBody CreateGameRequest createGameRequest) {
        log.info("Create game: {}", createGameRequest);

        var createdGame = gameService.addGame(createGameRequest);
        log.info("Game created {}", createdGame);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGame(@PathVariable Long id) throws Exception {
        log.info("Remove game with id {}", id);
        gameService.removeGame(id);
    }
}
