package poker.game.texasholdem;

import java.util.HashMap;
import java.util.Map;

public class THPot {
    private int total;
    private final Map<THPlayer, Integer> playerBets = new HashMap<>();

    public void addPlayerBet(THPlayer player, int bet) {
        total += bet;
        if (!playerBets.containsKey(player)) {
            playerBets.put(player, bet);
        } else {
            playerBets.put(player, playerBets.get(player) + bet);
        }
    }

    public void refresh() {
        total = 0;
        playerBets.clear();
    }

    public void distributeReward(Map<THPlayer, HandEvaluator> winners) {
        int splitReward = total / winners.size();
        for (Map.Entry<THPlayer, HandEvaluator> pair : winners.entrySet()) {
            pair.getKey().takeReward(splitReward);
        }
    }

    @Override
    public String toString() {
        return "THPot{total: " + total + ", players bet:{" + playersBet() + "}";
    }

    private String playersBet() {
        if (playerBets.isEmpty()) return null;

        var sb = new StringBuilder();
        for (Map.Entry<THPlayer, Integer> pair : playerBets.entrySet()) {
            sb.append("id: ").append(pair.getKey().getId())
                .append(", bet: ").append(pair.getValue())
                .append("; ");
        }
        sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }
}
