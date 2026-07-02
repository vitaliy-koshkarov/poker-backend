package poker.core.game.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Suit {
    CLUBS("♣", "Clubs", 0),
    DIAMONDS("♦", "Diamonds", 1),
    HEARTS("♥", "Hearts", 2),
    SPADES("♠", "Spades", 3);

    private final String symbol;
    private final String shortName;

    /**
     * Used to evaluate card combinations
     */
    private final int suitOrdinal;

    @Override
    public String toString() {
        return symbol;
    }
}
