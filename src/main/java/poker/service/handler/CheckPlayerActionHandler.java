package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;
import poker.model.Player;

import java.util.List;

@Component(value = PlayerActions.CHECK)
@Log4j2
@ToString
public class CheckPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Player actionInitiatorPlayer, List<Player> players) {
        long playerId = actionInitiatorPlayer.getId();
        log.info("Player id {} {} game id {}", playerId, PlayerActions.CHECK, game.getId());
        log.info("{}", gameEngine.getTable());


        log.info("Player id {} {} game id {}", playerId, PlayerActions.CHECK, game.getId());
    }
}
