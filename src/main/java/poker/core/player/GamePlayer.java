package poker.core.player;

import poker.core.Snapshot;
import poker.core.game.card.Card;

import java.util.List;

public interface GamePlayer extends Snapshot<GamePlayer> {
    long getId();
    String getNickname();
    PlayerStatus getStatus();
    void setStatus(PlayerStatus status);
    int getChips();
    int getCurrentBet();
    List<Card> getCards();

    void refresh();

    void setChips(int chips);

    void bet(int bet);

    void takeReward(int reward);
}
