package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.game.GamePot;
import poker.core.game.GameTable;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.service.*;

import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
@ToString
public class RaisePlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final PotService potService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.RAISE;
    }

    @Override
    public boolean handleAction(GameTable gameTable, PlayerActionData pad) {
        long gameId = gameTable.getId();
        long playerId = pad.getPlayerId();
        long roundId = gameTable.getRound().getId();
        int roundNumber = gameTable.getRound().getRoundNumber();
        long lastAggressorPlayerId = gameTable.getRound().getLastAggressorPlayerId();
        int lastMaxBet = gameTable.getRound().getLastMaxBet();
        Set<Long> playersToAct = gameTable.getRound().getPlayersToAct();

        gameService.updateActivePlayer(gameId, gameTable.getActivePlayerId());

        roundService.updateRound(roundId, roundNumber, lastAggressorPlayerId, lastMaxBet, playersToAct);

        GamePlayer player = gameTable.getPlayerById(playerId);
        int currentBet = player.getCurrentBet();
        playerService.updateStatusAndChipsAndCurrentBet(player.getId(), player.getStatus(), player.getChips(), currentBet);

        GamePot pot = gameTable.getPot();
        playerBetService.updatePlayerBet(playerId, pot.getId(), currentBet);

        potService.updatePotTotal(pot.getId(), pot.getTotal());

        long eventId = gameEventService.createAndSaveEvent(gameTable, pad);

        log.info("Player id {} {} game id {} pot id {} event id {}",
            playerId, pad.getPlayerAction(), gameId, pot.getId(), eventId);

        return true;
    }
}
