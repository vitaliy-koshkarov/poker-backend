package poker.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import poker.model.GameStatus;
import poker.model.PlayerStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Log4j2
public class THTable {
    private final long id;

    @Setter
    private GameStatus gameStatus;

    private final Deck deck;
    private final List<Card> communityCards;

    private final THPot pot;

    private final List<THPlayer> players;
    private int currentPlayerIdx;
    private int currentPlayerPosition;

    private int dealerIdx;
    private int dealerPosition;

    private int smallBlindIdx;
    private int smallBlindPosition;

    private int bigBlindIdx;
    private int bigBlindPosition;

    private int smallBlind;
    private int bigBlind;

    private int minRaise;

    public THTable(long id, GameStatus gameStatus, int smallBlind, int bigBlind) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.deck = new THDeck();
        this.communityCards = new ArrayList<>();
        this.pot = new THPot();
        this.players = new ArrayList<>();
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.minRaise = bigBlind;

        this.dealerIdx = 0;
        this.dealerPosition = 1;

        this.smallBlindIdx = 1;
        this.smallBlindPosition = 2;

        this.bigBlindIdx = 2;
        this.bigBlindPosition = 3;
    }

    public void removePlayer(THPlayer player) {
        players.remove(player);
    }

    public List<THPlayer> getActivePlayers() {
        var activePlayers = new LinkedList<THPlayer>();
        for (THPlayer player : players) {
            if (player.getStatus() == PlayerStatus.ACTIVE) {
                activePlayers.add(player);
            }
        }
        return activePlayers;
    }

    public void startNewGame() {
        pot.refresh();

        communityCards.clear();
        players.forEach(THPlayer::refresh);

        deck.shuffle();

        betBlinds();
        defineCurrentPlayer();
        dealStartHands();

        gameStatus = GameStatus.PRE_FLOP;
    }

    public void moveDealer() {
        dealerIdx++;
        if (dealerIdx >= players.size() - 1) {
            dealerIdx = 0;
        }
        dealerPosition = dealerIdx + 1;

        smallBlindIdx = (dealerIdx + 1) % players.size();
        smallBlindPosition = smallBlindIdx + 1;

        bigBlindIdx = (dealerIdx + 2) % players.size();
        bigBlindPosition = bigBlindIdx + 1;
        log.debug("Game {}, dealer position {}", id, dealerPosition);
    }

    @Override
    public String toString() {
        return "TexasHoldemTable{id " + id + ", deck size " + deck.getSize()
            + ", community cards " + communityCards + ", " + pot
            + ", Players{" + playersInfo() + ", current player idx " + currentPlayerIdx
            + ", current player position " + currentPlayerPosition
            + ", dealer position " + dealerPosition + ", small blind " + smallBlind
            + ", big blind " + bigBlind + ", min raise " + minRaise
            + "}";
    }

    void dealStartHands() {
        for (int i = 0; i < 2; i++) {
            for (THPlayer player : players) {
                player.getCards().add(deck.dealCard());
            }
        }
    }

    void betBlinds() {
        setBlind(getSmallBlindPosition(), smallBlind);
        setBlind(getBigBlindPosition(), bigBlind);

        minRaise = bigBlind;
    }

    void defineCurrentPlayer() {
        currentPlayerIdx++;
        if (currentPlayerIdx >= players.size() - 1) {
            currentPlayerIdx = 0;
        }
        currentPlayerPosition = currentPlayerIdx + 1;
    }

    private void setBlind(int blindPosition, int blind) {
        var smallBlindPlayer = players.get(blindPosition);
        smallBlindPlayer.bet(blind);
        pot.addPlayerBet(smallBlindPlayer, blind);
    }

    private String playersInfo() {
        var sb = new StringBuilder();
        sb.append("amount: ").append(players.size()).append(", ");
        for (THPlayer p : players) {
            if (p != null) {
                sb.append(p).append(", ");
            } else {
                sb.append("null, ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}
