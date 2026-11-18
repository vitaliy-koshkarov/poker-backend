package poker.core.game.texasholdem;

import lombok.Getter;
import poker.core.game.card.Card;

import java.util.List;

import static poker.core.game.texasholdem.Combination.*;

@Getter
public class HandEvaluator implements Comparable<HandEvaluator> {
    private final Combination combination;

    /**
     * Encodes top ranks for tie resolution
     */
    private final long tiebreaker;

    /**
     * Used to compare the strength of combinations
     */
    private final int strength;

    public HandEvaluator(Combination combination, long tiebreaker) {
        this.combination = combination;
        this.tiebreaker = tiebreaker;
        this.strength = encodeStrength();
    }

    public static HandEvaluator evaluate(List<Card> cards) {
        long[] suits = new long[4];
        int[] rankCounts = new int[13];

        for (Card c : cards) {
            suits[c.suit().getSuitOrdinal()] |= 1L << c.rank().getSeniority();
            rankCounts[c.rank().getSeniority()]++;
        }

//        Flush check
        int flushSuit = -1;
        for (int s = 0; s < 4; s++) {
            if (Long.bitCount(suits[s]) >= 5) {
                flushSuit = s;
                break;
            }
        }

        long allRanks = suits[0] | suits[1] | suits[2] | suits[3];
        int straightHigh = findStraightHigh(allRanks);

//        Straight Flush check
        if (flushSuit != -1) {
            int sfHigh = findStraightHigh(suits[flushSuit]);
            if (sfHigh != -1)
                return new HandEvaluator(STRAIGHT_FLUSH, tiebreakKey(sfHigh));
        }

//        Four or Full house or Trips or Pairs checks
        int four = -1, three = -1;
        int[] pairs = new int[2];
        int pairCount = 0;

        for (int r = 12; r >= 0; r--) {
            int count = rankCounts[r];
            if (count == 4) {
                four = r;
            } else if (count == 3 && three == -1) {
                three = r;
            } else if (count >= 2 && pairCount < 2) {
                pairs[pairCount++] = r;
            }
        }

        if (four != -1) {
            return new HandEvaluator(QUADS, tiebreakKey(four, kicker(rankCounts, four)));
        }

        if (three != -1 && pairCount >= 1) {
            return new HandEvaluator(FULL_HOUSE, tiebreakKey(three, pairs[0]));
        }

        if (flushSuit != -1) {
            return new HandEvaluator(FLUSH, topFiveKey(suits[flushSuit]));
        }

        if (straightHigh != -1) {
            return new HandEvaluator(STRAIGHT, tiebreakKey(straightHigh));
        }

        if (three != -1) {
            return new HandEvaluator(SET, tiebreakKey(three, topKickers(rankCounts, 3, three)));
        }

        if (pairCount >= 2) {
            return new HandEvaluator(TWO_PAIRS, tiebreakKey(pairs[0], pairs[1], topKickers(rankCounts, 1, pairs[0], pairs[1])));
        }

        if (pairCount == 1) {
            return new HandEvaluator(ONE_PAIR, tiebreakKey(pairs[0], topKickers(rankCounts, 3, pairs[0])));
        }

//        High Card
        return new HandEvaluator(HIGH_CARD, topKickers(rankCounts, 5));
    }

    @Override
    public int compareTo(HandEvaluator handEvaluator) {
        int cmp = Integer.compare(combination.ordinal(), handEvaluator.combination.ordinal());
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(tiebreaker, handEvaluator.tiebreaker);
    }

    @Override
    public String toString() {
        return "EvaluatedHand{" +
            "combination=" + combination +
            ", tiebreaker=" + tiebreaker +
            ", strength=" + strength +
            '}';
    }

    private int encodeStrength() {
//        Shift rank category into top 8 bits, then add lower bits for tie-breaking
        return (combination.ordinal() << 24) | (int) (tiebreaker & 0xFFFFFF);
    }

    private static int findStraightHigh(long mask) {
//        Handle wheel straight A2345
        if ((mask & 0b1000000001111L) == 0b1000000001111L) {
            return 3; // 5-high straight
        }

        for (int top = 12; top >= 4; top--) {
            long seq = 0b11111L << (top - 4);
            if ((mask & seq) == seq) {
                return top;
            }
        }

        return -1;
    }

    private static long tiebreakKey(long... ranks) {
        long key = 0;
        for (long r : ranks) {
            key = (key << 4) | r;
        }
        return key;
    }

    private static long topFiveKey(long suitMask) {
        long key = 0;
        int count = 0;
        for (int r = 12; r >= 0 && count < 5; r--) {
            if (((suitMask >> r) & 1) == 1) {
                key = (key << 4) | r;
                count++;
            }
        }
        return key;
    }

    private static long kicker(int[] rankCounts, int exclude) {
        for (int r = 12; r >= 0; r--) {
            if (r != exclude && rankCounts[r] > 0) {
                return r;
            }
        }
        return 0;
    }

    private static long topKickers(int[] rankCounts, int needed, int... exclude) {
        boolean[] skip = new boolean[13];
        for (int e : exclude) {
            skip[e] = true;
        }

        long key = 0;
        int count = 0;
        for (int r = 12; r >= 0 && count < needed; r--) {
            if (!skip[r] && rankCounts[r] > 0) {
                key = (key << 4) | r;
                count++;
            }
        }

        return key;
    }
}
