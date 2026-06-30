package poker.core.game;

import poker.core.game.texasholdem.HandEvaluator;
import poker.core.player.GamePlayer;

import java.util.Map;

public interface GamePot {
    long getId();

    void addPlayerBet(GamePlayer gamePlayer, int bet);

    void refresh();

    void distributeReward(Map<GamePlayer, HandEvaluator> winners);

    GamePot snapshot();
}
