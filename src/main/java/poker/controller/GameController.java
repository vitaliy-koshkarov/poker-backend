package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.service.GameTableService;

@RestController
@RequestMapping("/api/game")
@Log4j2
public class GameController {
    private final GameTableService gameTableService;

    public GameController(GameTableService gameTableService) {
        this.gameTableService = gameTableService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameTableDTO> getGameTableById(@PathVariable Long id) throws Exception {
        log.info("getGameTableById: {}", id);

        var gameTable = gameTableService.getGameTableById(id);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("Game table data: {}", gameTableDTO);

        return ResponseEntity.ok(gameTableDTO);
    }
}
