package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;
import poker.model.Player;

@Component(value = PlayerActions.ALL_INN)
@Log4j2
@ToString
public class AllInPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Player player) {
        long playerId = player.getId();
        log.info("Player id {} {} game id {}", playerId, PlayerActions.ALL_INN, game.getId());
        log.info("{}", gameEngine.getTable());


        log.info("Player id {} {} game id {}", playerId, PlayerActions.ALL_INN, game.getId());
    }
}
