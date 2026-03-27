package poker.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import poker.model.GameStatus;
import poker.model.PlayerStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static poker.model.GameStatus.*;

@Getter
public class THTable {
    private final long id;

    @Setter
    private GameStatus gameStatus;

    private final Deck deck;
    private final List<Card> communityCards;

    private final THPot pot;

    private final List<THPlayer> players;
    private final int maxPlayers;
    private int currentPlayer;

    private int dealerPosition;

    private int smallBlind;
    private int bigBlind;

    private int minRaise;

    public THTable(int id, GameStatus gameStatus, int maxPlayers, int smallBlind, int bigBlind) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.deck = new THDeck();
        this.communityCards = new ArrayList<>();
        this.pot = new THPot();
        this.players = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.minRaise = bigBlind;
    }

    public void addPlayer(THPlayer player) {
        if (gameStatus == WAITING_FOR_PLAYERS) {
            players.add(player);
        }
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

        moveDealer();

        deck.shuffle();

        betBlinds();
        dealStartHands();
    }

    @Override
    public String toString() {
        return "TexasHoldemTable{id: " + id + ", status: " + gameStatus + ", deck size: " + deck.getSize()
            + ", community cards: " + communityCards + ", " + pot
            + ", Players{" + playersInfo() + ", current player position: " + currentPlayer
            + ", dealer position: " + dealerPosition + ", small blind: " + smallBlind
            + ", big blind: " + bigBlind + ", min raise: " + minRaise
            + "}";
    }

    private void dealStartHands() {
        for (int i = 0; i < 2; i++) {
            for (THPlayer player : players) {
                player.getCards().add(deck.dealCard());
            }
        }
    }

    private void betBlinds() {
        setBlind(getSmallBlindPosition(), smallBlind);
        setBlind(getBigBlindPosition(), bigBlind);

        minRaise = bigBlind;

        currentPlayer = (dealerPosition + 3) % players.size();
    }

    private void moveDealer() {
        dealerPosition = (dealerPosition + 1) % players.size();
    }

    private int getSmallBlindPosition() {
        return (dealerPosition + 1) % players.size();
    }

    private int getBigBlindPosition() {
        return (dealerPosition + 2) % players.size();
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
