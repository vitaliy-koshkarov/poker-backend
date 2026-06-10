package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.game.texasholdem.THPlayer;
import poker.game.texasholdem.THTable;
import poker.model.Game;
import poker.model.Player;

@Component(value = PlayerActions.JOIN_GAME)
@Log4j2
@ToString
public class JoinPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Player player) {
        long playerId = player.getId();
        var thPlayer = new THPlayer(playerId, player.getNickname(), game.getBuyIn());
        THTable thTable = gameEngine.getTable();
        thTable.addPlayer(thPlayer);
        log.info("Player id {} {} game id {}", playerId, PlayerActions.JOIN_GAME, game.getId());
        log.info("{}", gameEngine.getTable());


//        player updates in SUBSCRIBE Controller method
        log.info("Player id {} {} game id {}", player.getId(), PlayerActions.JOIN_GAME, game.getId());
    }
}
