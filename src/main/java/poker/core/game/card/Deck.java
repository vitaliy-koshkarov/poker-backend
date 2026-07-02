package poker.core.game.card;

public interface Deck {
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

    Deck snapshot();
}
