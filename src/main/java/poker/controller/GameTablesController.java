package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.dto.game.CreateGameTableRequest;
import poker.dto.game.GameTableDTO;
import poker.service.GameTableService;
import poker.util.Util;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Log4j2
public class GameTablesController {
    private final GameTableService gameTableService;

    public GameTablesController(GameTableService gameTableService) {
        this.gameTableService = gameTableService;
    }

    @GetMapping
    public List<GameTableDTO> getGameTables() {
        log.info("Get list of games");
        return gameTableService.getGameTablesList();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGameTable(@RequestBody CreateGameTableRequest createGameTableRequest) {
        log.info("Create game table: {}", createGameTableRequest);
        gameTableService.createGameTable(createGameTableRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGameTable(@PathVariable Long id) {
        long userId = Util.getPlayerDetailsFronCtx().getId();
        log.info("Remove game request, game table id {}, user id {}", id, userId);
        gameTableService.removeGameTable(id);
    }
}
