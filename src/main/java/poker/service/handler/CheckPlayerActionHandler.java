package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.service.GameEventService;
import poker.service.GameService;
import poker.service.PlayerService;
import poker.service.RoundService;
import poker.util.Util;

import java.util.List;

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
    public boolean handleAction(GameTable gameTable, PlayerActionData pad) {
        long gameId = gameTable.getId();
        long playerId = pad.getPlayerId();
        long roundId = gameTable.getRound().getId();
        int roundNumber = gameTable.getRound().getRoundNumber();
        long lastAggressorPlayerId = gameTable.getRound().getLastAggressorPlayerId();
        int lastMaxBet = gameTable.getRound().getLastMaxBet();
        List<Long> playersToAct = gameTable.getRound().getPlayersToAct();

        gameService.updateActivePlayer(gameId, gameTable.getActivePlayerId());

        roundService.updateRound(roundId, roundNumber, lastAggressorPlayerId, lastMaxBet, playersToAct);

        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.CHECK, Util.INT_ZERO);

        long eventId = gameEventService.createAndSaveEvent(gameTable, pad);

        log.info("Player id {} {} status {} game id {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.CHECK, gameId, eventId);

        return true;
    }
}
