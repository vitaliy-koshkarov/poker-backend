package poker.game;

import poker.game.texasholdem.Card;

import java.util.List;

public interface GamePlayer {
    long getId();
    String getNickname();
    int getStatus();
    int getChips();
    int getCurrentBet();
    List<Card> getCards();
}
