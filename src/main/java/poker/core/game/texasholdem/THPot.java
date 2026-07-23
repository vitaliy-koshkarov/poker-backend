package poker.core.game.texasholdem;

import lombok.Getter;
import poker.core.game.GamePot;
import poker.core.player.GamePlayer;

import java.util.HashMap;
import java.util.Map;

@Getter
public class THPot implements GamePot {
    private final long id;
    /**
     * Key - {@link GamePlayer#getId()}, value - player bet
     */
    private final Map<Long, Integer> playersBets = new HashMap<>();
    private int total;

    public THPot(long id) {
        this.id = id;
    }

    @Override
    public void addPlayerBet(long playerId, int bet) {
        total += bet;

        if (!playersBets.containsKey(playerId)) {
            playersBets.put(playerId, bet);
        } else {
            playersBets.put(playerId, playersBets.get(playerId) + bet);
        }
    }

    @Override
    public void refresh() {
        total = 0;
        playersBets.clear();
    }

    @Override
    public void distributeReward(Map<GamePlayer, HandEvaluator> winners) {
        int splitReward = total / winners.size();
        for (Map.Entry<GamePlayer, HandEvaluator> pair : winners.entrySet()) {
            pair.getKey().takeReward(splitReward);
        }
    }

    @Override
    public GamePot snapshot() {
        GamePot pot = new THPot(id);
        playersBets.forEach(pot::addPlayerBet);
        return pot;
    }

    @Override
    public String toString() {
        return "THPot{id: " + id + ", total: " + total + ", players bet:{" + playersBet() + "}";
    }

    private String playersBet() {
        if (playersBets.isEmpty()) return null;

        var sb = new StringBuilder();
        for (Map.Entry<Long, Integer> pair : playersBets.entrySet()) {
            sb.append("id: ").append(pair.getKey())
                .append(", bet: ").append(pair.getValue())
                .append("; ");
        }
        sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }
}
