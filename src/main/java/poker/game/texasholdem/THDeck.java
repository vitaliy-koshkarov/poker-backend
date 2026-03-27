package poker.game.texasholdem;

import java.util.Random;

public class THDeck implements Deck {
    private int deckSize;
    private final Card[] deck;

    public THDeck() {
        deckSize = 52;
        deck = new Card[deckSize];

        int i = 0;
        for (Rank rank : Rank.values()) {
            for (Suit suit : Suit.values()) {
                deck[i] = new Card(rank, suit);
                i++;
            }
        }
    }

    @Override
    public int getSize() {
        return deckSize;
    }

    @Override
    public Card dealCard() {
        return deck[--deckSize];
    }

    @Override
    public void shuffle() {
        deckSize = 52;
        var random = new Random();
//        TODO: may be there is a better way to shuffle deck
        for (int i = 0; i < deckSize; i++) {
            int randomIdx = random.nextInt(0, deckSize);
            var tempCard = deck[randomIdx];
            deck[randomIdx] = deck[i];
            deck[i] = tempCard;
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Deck size: ").append(deckSize);
        for (int i = 0; i < deckSize; i++) {
            if (i % 4 == 0) {
                sb.append("\r\n");
            }
            sb.append(deck[i].toString()).append(" ");
        }
        return sb.toString();
    }
}
