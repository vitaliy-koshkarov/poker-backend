package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.model.Game;
import poker.model.PlayerDetails;
import poker.service.GameService;

@Component("JoinPlayerActionHandler")
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements PlayerActionHandler {
    private final GameService gameService;

    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        engineHandling(gameEngine, game, playerDetails);

        repositoryHandling(game, playerDetails);
    }

    private void engineHandling(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
//        var player = playerDetails.getPlayer();
//        long playerId = playerDetails.getPlayer().getId();
//        var thPlayer = new THPlayer(playerId, player.getNickname(), game.getBuyIn());
//        THTable thTable = gameEngine.getTable();
//        thTable.addPlayer(thPlayer);
//        log.info("Player id {} {} game id {}", playerId, PlayerActions.JOIN_GAME, game.getId());
//        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(Game game, PlayerDetails playerDetails) {
        gameService.joinPlayerToGame(game.getId(), playerDetails);
        log.info("Player id {} {} game id {}", playerDetails.getPlayer().getId(), PlayerAction.JOIN_GAME.getActionName(), game.getId());
    }
}
