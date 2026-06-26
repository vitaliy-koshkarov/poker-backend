package poker.core.player;

import poker.core.game.card.Card;

import java.util.List;

public interface GamePlayer {
    long getId();
    String getNickname();
    PlayerStatus getStatus();
    void setStatus(PlayerStatus status);
    int getChips();
    int getCurrentBet();
    List<Card> getCards();

    void refresh();

    void bet(int bet);

    void takeReward(int reward);
}
