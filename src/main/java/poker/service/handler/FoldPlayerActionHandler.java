package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.service.PlayerBetService;
import poker.service.PlayerService;
import poker.util.Util;

@Component("FOLD")
@RequiredArgsConstructor
@Log4j2
@ToString
public class FoldPlayerActionHandler implements DBPlayerActionHandler {
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.FOLD, Util.DEFAULT_INT_VALUE);
        playerBetService.updatePlayerBet(playerId, gameEngine.getTable().getPot().getId(), Util.DEFAULT_INT_VALUE);
        log.info("Player id {} status {} current bet {}", playerId, PlayerStatus.FOLD, Util.DEFAULT_INT_VALUE);

        log.info("Player id {} {} game id {}",
            playerId, PlayerAction.FOLD.getActionName(), gameEngine.getTable().getId());

        return true;
    }
}
