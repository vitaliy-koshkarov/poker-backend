package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.Game;
import poker.model.PlayerDetails;
import poker.service.GameService;

@Component("JOIN")
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
//        TODO: refactoring parameters of join player to the game
//        gameService.joinPlayerToGame(gameId, playerDetails);
        log.info("Player id {} {} game id {}",
            pad.getPlayerDetails().getPlayer().getId(), PlayerAction.JOIN_GAME.getActionName(), gameId);

        return true;
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
}
