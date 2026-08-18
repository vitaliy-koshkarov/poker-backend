package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import poker.core.Snapshot;
import poker.util.Util;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class THRound implements Snapshot<THRound> {
    private long id;
    private long gameId;
    private int roundNumber;
    private long lastAggressorPlayerId;
    private int lastMaxBet;
    private Set<Long> playersToAct;

    public THRound(long id, long gameId, long playerId, int bet) {
        this.id = id;
        this.gameId = gameId;
        this.roundNumber = Util.INT_ONE;
        this.lastAggressorPlayerId = playerId;
        this.lastMaxBet = bet;
        this.playersToAct = new LinkedHashSet<>();
    }

    private THRound(long id, long gameId, int roundNumber, long playerId, int bet, Set<Long> playersToAct) {
        this.id = id;
        this.gameId = gameId;
        this.roundNumber = roundNumber;
        this.lastAggressorPlayerId = playerId;
        this.lastMaxBet = bet;
        this.playersToAct = new LinkedHashSet<>(playersToAct);
    }

    public void addPlayersToAct(long playerId) {
        playersToAct.add(playerId);
    }

    public void removePlayerToAct(long playerId) {
        playersToAct.remove(playerId);
    }

    public void refresh() {
        lastAggressorPlayerId = Util.LONG_ZERO;
        lastMaxBet = Util.INT_ZERO;
        playersToAct.clear();
    }

    public void increment() {
        roundNumber += Util.INT_ONE;
    }

    @Override
    public THRound snapshot() {
        return new THRound(id, gameId, roundNumber, lastAggressorPlayerId, lastMaxBet, playersToAct);
    }
}
