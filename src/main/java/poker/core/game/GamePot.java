package poker.core.game;

import poker.core.player.GamePlayer;

public interface GamePot {
    void addPlayerBet(GamePlayer gamePlayer, int bet);

    void refresh();
}
