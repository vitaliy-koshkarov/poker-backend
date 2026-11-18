package poker.core.game.texasholdem;

import poker.core.game.card.Card;

import java.util.List;

public record WinnerPlayer(
    long id,
    int reward,
    List<Card> cards) {
}
