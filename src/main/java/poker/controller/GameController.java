package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.model.User;
import poker.service.GameTableService;
import poker.service.UserService;
import poker.util.Util;

@RestController
@RequestMapping("/api/game")
@Log4j2
public class GameController {
    private final GameTableService gameTableService;
    private final UserService userService;

    public GameController(GameTableService gameTableService, UserService userService) {
        this.gameTableService = gameTableService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameTableDTO> getGameTableById(@PathVariable Long id) {
        log.info("getGameTableById: {}", id);

        Long userId = Util.getPlayerDetailsFronCtx().getId();
        log.info("User id {}", userId);

        var gameTable = gameTableService.getGameTableById(id);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("Game table data: {}", gameTableDTO);

        User user = userService.getUserPlayerById(userId);
        log.info("User: {}", user);

        return ResponseEntity.ok(gameTableDTO);
    }
}
