package poker.core.game;

import poker.core.Snapshot;
import poker.core.game.texasholdem.HandEvaluator;
import poker.core.player.GamePlayer;

import java.util.Map;

public interface GamePot extends Snapshot<GamePot> {
    long getId();
    int getTotal();
    Map<Long, Integer> getPlayersBets();

    void addPlayerBet(long playerId, int bet);

    void refresh();

    void distributeReward(Map<GamePlayer, HandEvaluator> winners);
}
