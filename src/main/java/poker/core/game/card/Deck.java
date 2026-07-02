package poker.core.game.card;

import poker.core.Snapshot;

public interface Deck extends Snapshot<Deck> {
    /**
     * @return current deck size
     */
    int getSize();

    /**
     * @return a card from the top of the deck
     */
    Card dealCard();

    /**
     * Shuffling the deck
     */
    void shuffle();
}
