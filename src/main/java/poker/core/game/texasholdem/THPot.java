package poker.core.game.texasholdem;

import lombok.Getter;
import poker.core.game.GamePot;
import poker.core.player.GamePlayer;

import java.util.HashMap;
import java.util.Map;

@Getter
public class THPot implements GamePot {
    private final long id;
    private int total;
//    todo: key - player's id
    private final Map<GamePlayer, Integer> playersBets = new HashMap<>();

    public THPot(long id) {
        this.id = id;
    }

    @Override
    public void addPlayerBet(GamePlayer player, int bet) {
        total += bet;
        if (!playersBets.containsKey(player)) {
            playersBets.put(player, bet);
        } else {
            playersBets.put(player, playersBets.get(player) + bet);
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
        for (Map.Entry<GamePlayer, Integer> pair : playersBets.entrySet()) {
            sb.append("id: ").append(pair.getKey().getId())
                .append(", bet: ").append(pair.getValue())
                .append("; ");
        }
        sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }
}
