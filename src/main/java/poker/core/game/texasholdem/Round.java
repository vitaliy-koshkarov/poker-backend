package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import poker.core.Snapshot;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class Round implements Snapshot<Round> {
    private long lastAggressorPlayerId;
    private int lastMaxBet;
    private Set<Long> playersToAct;

    public Round(long playerId, int bet) {
        lastAggressorPlayerId = playerId;
        lastMaxBet = bet;
        playersToAct = new HashSet<>();
    }

    private Round(long playerId, int bet, Set<Long> playersToAct) {
        lastAggressorPlayerId = playerId;
        lastMaxBet = bet;
        this.playersToAct = new HashSet<>(playersToAct);
    }

    public void addPlayerToAct(long playerId) {
        playersToAct.add(playerId);
    }

    public void removePlayerToAct(long playerId) {
        playersToAct.remove(playerId);
    }

    @Override
    public Round snapshot() {
        return new Round(lastAggressorPlayerId, lastMaxBet, playersToAct);
    }
}
