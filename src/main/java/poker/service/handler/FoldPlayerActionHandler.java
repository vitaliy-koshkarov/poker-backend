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
import poker.service.GameEventService;
import poker.service.GameService;
import poker.service.PlayerBetService;
import poker.service.PlayerService;
import poker.util.Util;

@Component("FOLD")
@RequiredArgsConstructor
@Log4j2
@ToString
public class FoldPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final GameEventService gameEventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        gameService.updateActivePlayer(gameId, gameEngine.getTable().getActivePlayerId());
        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.FOLD, Util.DEFAULT_INT_VALUE);
        playerBetService.updatePlayerBet(playerId, gameEngine.getTable().getPot().getId(), Util.DEFAULT_INT_VALUE);

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} status {} game id {} current bet {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.FOLD, gameId, Util.DEFAULT_INT_VALUE, eventId);

        return true;
    }
}
