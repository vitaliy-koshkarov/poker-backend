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
import poker.service.*;
import poker.util.Util;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
@ToString
public class FoldPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.FOLD;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.table().getId();
        long playerId = pad.getPlayerId();
        long roundId = gameEngine.table().getRound().getId();
        int roundNumber = gameEngine.table().getRound().getRoundNumber();
        long lastAggressorPlayerId = gameEngine.table().getRound().getLastAggressorPlayerId();
        int lastMaxBet = gameEngine.table().getRound().getLastMaxBet();
        Set<Long> playersToAct = gameEngine.table().getRound().getPlayersToAct();

        gameService.updateActivePlayer(gameId, gameEngine.table().getActivePlayerId());

        roundService.updateRound(roundId, roundNumber, lastAggressorPlayerId, lastMaxBet, playersToAct);

        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.FOLD, Util.INT_ZERO);

        playerBetService.updatePlayerBet(playerId, gameEngine.table().getPot().getId(), Util.INT_ZERO);

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} status {} game id {} current bet {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.FOLD, gameId, Util.INT_ZERO, eventId);

        return true;
    }
}
