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
import poker.service.*;
import poker.util.Util;

import java.util.List;

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

        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.FOLD, Util.INT_ZERO);

        playerBetService.updatePlayerBet(playerId, gameTable.getPot().getId(), Util.INT_ZERO);

        long eventId = gameEventService.createAndSaveEvent(gameTable, pad);

        log.info("Player id {} {} status {} game id {} current bet {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.FOLD, gameId, Util.INT_ZERO, eventId);

        return true;
    }
}
