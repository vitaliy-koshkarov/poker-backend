package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.model.PlayerDetails;
import poker.model.User;
import poker.service.GameTableService;
import poker.service.UserService;

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
    public ResponseEntity<GameTableDTO> getGameTableById(@PathVariable Long id) throws Exception {
        log.info("getGameTableById: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authentication);
        Long userId = ((PlayerDetails) authentication.getPrincipal()).getId();
        log.info("User id {}", userId);

        var gameTable = gameTableService.getGameTableById(id);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("Game table data: {}\r\n", gameTableDTO);

        User user = userService.getUserPlayerById(userId);
        log.info("User: {}\r\n", user);

        return ResponseEntity.ok(gameTableDTO);
    }
}
