package poker.game.texasholdem;

public record Card(Rank rank, Suit suit) implements Comparable<Card> {
    @Override
    public String toString() {
        return rank.getShortName() + suit.getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int compareTo(Card card) {
        return Integer.compare(rank.getSeniority(), card.rank().getSeniority());
    }
}
