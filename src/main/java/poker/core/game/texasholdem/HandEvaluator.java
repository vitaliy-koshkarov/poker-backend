package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import poker.core.game.card.Card;

import java.util.List;

@NoArgsConstructor
@Getter
public class HandEvaluator {
    private Combination combination;

    /**
     * Encodes top ranks for tie resolution
     */
    private long tiebreaker;

    /**
     * Used to compare the strength of combinations
     */
    private int strength;

    public static HandEvaluator evaluate(List<Card> cards) {
        return new HandEvaluator();
    }
}
