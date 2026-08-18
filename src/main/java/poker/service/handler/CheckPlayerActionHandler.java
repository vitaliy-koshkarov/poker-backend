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
import poker.service.PlayerService;
import poker.service.RoundService;
import poker.util.Util;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
@ToString
public class CheckPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CHECK;
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

        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.CHECK, Util.INT_ZERO);

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} status {} game id {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.CHECK, gameId, eventId);

        return true;
    }
}
