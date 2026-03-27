package poker.game.texasholdem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Rank {
    _2("2", "Two", 0),
    _3("3", "Three", 1),
    _4("4", "Four", 2),
    _5("5", "Five", 3),
    _6("6", "Six", 4),
    _7("7", "Seven", 5),
    _8("8", "Eight", 6),
    _9("9", "Nine", 7),
    _10("10", "Ten", 8),
    J("J", "Jack", 9),
    Q("Q", "Queen", 10),
    K("K", "King", 11),
    A("A", "Ace", 12);

    private final String shortName;
    private final String name;

    /**
     * Used to evaluate card combinations.
     */
    private final int seniority;

    @Override
    public String toString() {
        return shortName;
    }
}
