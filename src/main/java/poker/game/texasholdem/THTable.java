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
//    TODO: map: [playerId -> playerPosition] on the table (in 'players' list) ?

    private final long id;

    @Setter
    private GameStatus gameStatus;

    private final Deck deck;
    private final List<Card> communityCards;

    private final THPot pot;

    private final List<THPlayer> players;

    private int dealerIdx;
    private int smallBlindIdx;
    private int bigBlindIdx;
    private int activePlayerIdx;

    private int smallBlind;
    private int bigBlind;

    private int minRaise;

    public THTable(long id, THPot thPot, GameStatus gameStatus, int smallBlind, int bigBlind) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.deck = new THDeck();
        this.communityCards = new ArrayList<>();
        this.pot = thPot;
        this.players = new ArrayList<>();
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
    }

    public void addPlayer(THPlayer thPlayer) {
        players.add(thPlayer);
    }

    public void removePlayer(Long playerId) {
        THPlayer thPlayerToRemove = null;
        for (THPlayer thPlayer : players) {
            if (thPlayer.getId() == playerId) {
                thPlayerToRemove = thPlayer;
                break;
            }
        }
        players.remove(thPlayerToRemove);
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

    public void setUpNewRound() {
        pot.refresh();

        communityCards.clear();
        players.forEach(THPlayer::refresh);

        deck.shuffle();

        defineDealerIdx();
        defineBlindIndices();
        defineActivePlayer();

        betBlinds();
        minRaise = bigBlind;
        dealStartHands();

        gameStatus = GameStatus.PRE_FLOP;
    }

    @Override
    public String toString() {
        return "TexasHoldemTable{id " + id + ", deck size " + deck.getSize()
            + ", community cards " + communityCards + ", " + pot
            + ", Players{" + playersInfo() + ", dealer idx " + dealerIdx
            + ", small blind idx " + smallBlindIdx + ", big blind idx " + bigBlindIdx
            + ", active player idx " + activePlayerIdx + ", min raise " + minRaise
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
        setBlind(getSmallBlindIdx(), smallBlind);
        setBlind(getBigBlindIdx(), bigBlind);
    }

    private void setBlind(int blindIdx, int blind) {
        var playerWithBlind = players.get(blindIdx);
        playerWithBlind.bet(blind);
        pot.addPlayerBet(playerWithBlind, blind);
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

    private void defineDealerIdx() {
        dealerIdx++;
        if (dealerIdx >= players.size()) {
            dealerIdx = 0;
        }
    }

    private void defineBlindIndices() {
        smallBlindIdx = dealerIdx + 1;
        if (smallBlindIdx >= players.size()) {
            smallBlindIdx = 0;
        }

        bigBlindIdx = smallBlindIdx + 1;
        if (bigBlindIdx >= players.size()) {
            bigBlindIdx = 0;
        }
    }

    private void defineActivePlayer() {
        activePlayerIdx = bigBlindIdx + 1;
        if (activePlayerIdx >= players.size()) {
            activePlayerIdx = 0;
        }
        players.get(activePlayerIdx)
            .setStatus(PlayerStatus.ACTIVE);
    }
}
