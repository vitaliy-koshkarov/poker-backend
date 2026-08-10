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
    private long lastAggressorPlayerId;
    private int lastMaxBet;
    private Set<Long> playersToAct;

    public THRound(long id, long gameId, long playerId, int bet) {
        this.id = id;
        this.gameId = gameId;
        this.lastAggressorPlayerId = playerId;
        this.lastMaxBet = bet;
        this.playersToAct = new LinkedHashSet<>();
    }

    private THRound(long id, long gameId, long playerId, int bet, Set<Long> playersToAct) {
        this.id = id;
        this.gameId = gameId;
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
        lastAggressorPlayerId = Util.ZERO_LONG;
        lastMaxBet = Util.ZERO_INT;
        playersToAct.clear();
    }

    @Override
    public THRound snapshot() {
        return new THRound(id, gameId, lastAggressorPlayerId, lastMaxBet, playersToAct);
    }
}
