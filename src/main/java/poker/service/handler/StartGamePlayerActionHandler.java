package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.game.texasholdem.THEngine;
import poker.model.Game;
import poker.model.GameStatus;
import poker.service.GameService;

import java.sql.Timestamp;

@Component(value = PlayerActions.START_GAME)
@Log4j2
@ToString
public class StartGamePlayerActionHandler implements PlayerActionHandler {
    private final GameService gameService;

    @Autowired
    public StartGamePlayerActionHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handleAction(GameEngine gameEngine, Game game, Long playerId) {
        long gameId = game.getId();

        game.setStatus(GameStatus.PRE_FLOP.getStatus());
        game.setStartedAt(new Timestamp(System.currentTimeMillis()));

        long dealerId = ((THEngine) gameEngine).getDealerId();
        game.setDealerId(dealerId);

        long activePlayerId = ((THEngine) gameEngine).getActivePlayerId();
        game.setActivePlayerId(activePlayerId);

        gameService.updateGame(game);
//         TODO: update player's statuses
//         playerService.updatePlayers(players);
//         gameService.updateGame(game);
        log.info("Player id {} {} game id {}", playerId, PlayerActions.START_GAME, gameId);
    }
}
