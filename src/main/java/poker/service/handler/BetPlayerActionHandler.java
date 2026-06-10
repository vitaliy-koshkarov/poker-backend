package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;
import poker.model.Player;

@Component(value = PlayerActions.BET)
@Log4j2
@ToString
public class BetPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Player player) {
        long playerId = player.getId();
        log.info("Player id {} {} game id {}", playerId, PlayerActions.BET, game.getId());
        log.info("{}", gameEngine.getTable());


        log.info("Player id {} {} game id {}", playerId, PlayerActions.BET, game.getId());
    }
}
