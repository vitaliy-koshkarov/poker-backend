package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.model.Game;
import poker.model.PlayerDetails;

@Component("BetPlayerActionHandler")
@Log4j2
@ToString
public class BetPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        engineHandling(gameEngine, playerDetails, game);

        repositoryHandling(playerDetails, game);
    }

    private void engineHandling(GameEngine gameEngine, PlayerDetails playerDetails, Game game) {
        long playerId = playerDetails.getPlayer().getId();
        log.info("Player id {} {} game id {}", playerId, PlayerAction.BET.getActionName(), game.getId());
//        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(PlayerDetails playerDetails, Game game) {
        log.info("Player id {} {} game id {}", playerDetails.getPlayer().getId(), PlayerAction.BET.getActionName(), game.getId());
    }
}
