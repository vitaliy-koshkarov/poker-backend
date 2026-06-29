package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import poker.core.game.GamePot;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Log4j2
public class THTable implements GameTable {
//    TODO: map: [playerId -> playerPosition] on the table (in 'players' list) ?

    private final long id;
    private final String name;
    private final long creatorPlayerId;

    private GameStatus gameStatus;

    private long dealerId;
    private int dealerIdx;

    private long activePlayerId;
    private int activePlayerIdx;

    private final int maxPlayers;

    private int smallBlind;
    private int smallBlindIdx;

    private int bigBlind;
    private int bigBlindIdx;

    private int minRaise;
    private final int buyIn;

    private final GamePot pot;
    private final List<GamePlayer> players;
    private final Deck deck;
    private final List<Card> communityCards;

    public THTable(long id, String name, long creatorPlayerId, int maxPlayers, int buyIn,
                   GamePot pot, GameStatus gameStatus, int smallBlind, int bigBlind) {
        this.id = id;
        this.name = name;
        this.creatorPlayerId = creatorPlayerId;
        this.gameStatus = gameStatus;
        this.deck = new THDeck();
        this.communityCards = new ArrayList<>();
        this.pot = pot;
        this.players = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.buyIn = buyIn;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
    }

    @Override
    public int getCurrentPlayersCount() {
        return players.size();
    }

    @Override
    public List<GamePlayer> getActivePlayers() {
        var activePlayers = new LinkedList<GamePlayer>();
        for (GamePlayer player : players) {
            if (player.getStatus() == PlayerStatus.ACTIVE) {
                activePlayers.add(player);
            }
        }
        return activePlayers;
    }

    @Override
    public void addPlayer(GamePlayer gamePlayer) {
        players.add(gamePlayer);
    }

    @Override
    public void removePlayer(long playerId) {
        GamePlayer playerToRemove = null;
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.getId() == playerId) {
                playerToRemove = gamePlayer;
                break;
            }
        }
        players.remove(playerToRemove);
    }

    @Override
    public void overrideActivePlayer() {
//        todo: implement
        activePlayerIdx++;
        if (activePlayerIdx >= players.size()) {
            activePlayerIdx = 0;
            players.get(activePlayerIdx).setStatus(PlayerStatus.ACTIVE);
        }
    }

    @Override
    public void updateGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public void dealStartHands() {
        for (int i = 0; i < 2; i++) {
            for (GamePlayer player : players) {
                player.getCards().add(deck.dealCard());
            }
        }
    }

    @Override
    public void betBlinds() {
        setBlind(getSmallBlindIdx(), smallBlind);
        setBlind(getBigBlindIdx(), bigBlind);
    }

    @Override
    public String toString() {
        return "THTable{id " + id + ", deck size " + deck.getSize()
            + ", community cards " + communityCards + ", " + pot
            + ", Players{" + playersInfo() + ", dealer idx " + dealerIdx
            + ", small blind idx " + smallBlindIdx + ", big blind idx " + bigBlindIdx
            + ", active player idx " + activePlayerIdx + ", min raise " + minRaise
            + "}";
    }

    public void setUpNewRound() {
        pot.refresh();

        communityCards.clear();
        players.forEach(GamePlayer::refresh);

        deck.shuffle();

        defineDealerIdx();
        defineBlindIndices();
        defineActivePlayer();

        betBlinds();
        minRaise = bigBlind;
        dealStartHands();

        gameStatus = GameStatus.PRE_FLOP;
    }

    private void setBlind(int blindIdx, int blind) {
        var playerWithBlind = players.get(blindIdx);
        playerWithBlind.bet(blind);
        pot.addPlayerBet(playerWithBlind, blind);
    }

    private String playersInfo() {
        var sb = new StringBuilder();
        sb.append("amount: ").append(players.size()).append(", ");
        for (GamePlayer p : players) {
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

        players.get(activePlayerIdx).setStatus(PlayerStatus.ACTIVE);
    }
}
