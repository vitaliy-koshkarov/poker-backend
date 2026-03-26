package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.util.Util;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Log4j2
public class GamesController {
    private final GameService gameService;

    public GamesController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameDTO> getGames() {
        log.info("Get list of games");
        return gameService.getGamesList();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@AuthenticationPrincipal Authentication authentication,
                                        @RequestBody CreateGameRequest createGameRequest) {
        var playerDetails = ((PlayerDetails) authentication.getPrincipal());
        Long creatorPlayerId = playerDetails.getPlayer().getId();
        log.info("Create game {} from player {}", createGameRequest, creatorPlayerId);
        gameService.createGame(creatorPlayerId, createGameRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteGame(@PathVariable Long id) {
        long userId = Util.getPlayerDetailsFronCtx().getUser().getId();
        log.info("Remove game request, game id {}, user id {}", id, userId);
        gameService.removeGame(id);
    }
}
