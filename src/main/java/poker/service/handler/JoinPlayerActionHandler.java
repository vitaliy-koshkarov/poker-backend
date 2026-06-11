package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.game.texasholdem.THPlayer;
import poker.game.texasholdem.THTable;
import poker.model.Game;
import poker.model.PlayerDetails;
import poker.service.GameService;

@Component(value = PlayerActions.JOIN_GAME)
@Log4j2
@ToString
public class JoinPlayerActionHandler implements PlayerActionHandler {
    private final GameService gameService;

    @Autowired
    public JoinPlayerActionHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        engineHandling(gameEngine, game, playerDetails);

        repositoryHandling(game, playerDetails);
    }

    private void engineHandling(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        var player = playerDetails.getPlayer();
        long playerId = playerDetails.getPlayer().getId();
        var thPlayer = new THPlayer(playerId, player.getNickname(), game.getBuyIn());
        THTable thTable = gameEngine.getTable();
        thTable.addPlayer(thPlayer);
        log.info("Player id {} {} game id {}", playerId, PlayerActions.JOIN_GAME, game.getId());
        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(Game game, PlayerDetails playerDetails) {
        gameService.joinPlayerToGame(game.getId(), playerDetails);
        log.info("Player id {} {} game id {}", playerDetails.getPlayer().getId(), PlayerActions.JOIN_GAME, game.getId());
    }
}
