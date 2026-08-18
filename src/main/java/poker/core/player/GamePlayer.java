package poker.core.player;

import poker.core.Snapshot;
import poker.core.game.card.Card;

import java.util.List;

public interface GamePlayer extends Snapshot<GamePlayer> {
    long getId();
    String getNickname();
    PlayerStatus getStatus();
    int getChips();
    int getCurrentBet();
    List<Card> getCards();

    void refresh();

    void setStatus(PlayerStatus status);
    void setChips(int chips);
    void setCurrentBet(int bet);

    void bet(int bet);

    void takeReward(int reward);
}
