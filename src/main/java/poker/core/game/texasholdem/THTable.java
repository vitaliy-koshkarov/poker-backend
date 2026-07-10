package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import poker.core.game.GamePot;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerStatus;
import poker.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Log4j2
public class THTable implements GameTable {
    private final long id;
    private final String name;
    private final long creatorPlayerId;
    private final int maxPlayers;
    private final int buyIn;

    @Setter
    private GameStatus gameStatus;

    @Setter
    private long dealerId;
    @Setter
    private int dealerIndex;

    @Setter
    private long activePlayerId;
    @Setter
    private int activePlayerIndex;

    @Setter
    private int smallBlind;
    @Setter
    private int smallBlindIndex;

    @Setter
    private int bigBlind;
    @Setter
    private int bigBlindIndex;

    @Setter
    private int minRaise;

    @Setter
    private GamePot pot;

//    TODO: use Map instead of list. Key - player id, value - GamePlayer
    @Setter
    private List<GamePlayer> players;
    @Setter
    private Deck deck;
    @Setter
    private List<Card> communityCards;
    @Setter
    private long[] playersSeats;

    public THTable(long id, String name, long creatorPlayerId, int maxPlayers, int buyIn,
                   GameStatus gameStatus, int smallBlind, int bigBlind, GamePot pot) {
        this.id = id;
        this.name = name;
        this.creatorPlayerId = creatorPlayerId;
        this.maxPlayers = maxPlayers;
        this.buyIn = buyIn;
        this.gameStatus = gameStatus;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.pot = pot;
        this.deck = new THDeck();
        this.communityCards = new ArrayList<>();
        this.players = new ArrayList<>();
        this.playersSeats = new long[maxPlayers];
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
    public int getPlayerSeatNumber(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == playerId) return i;
        }
        log.error("Player id {} seat number not found", playerId);
        return Util.INVALID_INT_VALUE; // TODO: throw ex and handle it above
    }

    @Override
    public void addPlayer(GamePlayer gamePlayer) {
        players.add(gamePlayer);
        seatPlayer(gamePlayer.getId());
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

        releaseSeat(playerId);
    }

    @Override
    public void overrideActivePlayer() {
        activePlayerIndex++;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
            if (!players.isEmpty()) {
                players.get(activePlayerIndex).setStatus(PlayerStatus.ACTIVE);
            }
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
    public void startGame() {
        gameStatus = GameStatus.PRE_FLOP;

        defineDealerIdx();

        players.forEach(GamePlayer::refresh);

        defineActivePlayer();

        players.forEach(player -> player.setChips(buyIn));

        defineBlindIndices();
        betBlinds();

        minRaise = bigBlind;

        deck.shuffle();
        dealStartHands();
    }

    @Override
    public void foldPlayer(long playerId) {
        for (GamePlayer gp : players) {
            if (gp.getId() == playerId) {
                gp.setStatus(PlayerStatus.FOLD);
                gp.setCurrentBet(Util.DEFAULT_INT_VALUE);
                break;
            }
        }
    }

    @Override
    public void betBlinds() {
        setBlindByIndex(getSmallBlindIndex(), smallBlind);
        setBlindByIndex(getBigBlindIndex(), bigBlind);
    }

    @Override
    public void checkPlayer(long playerId) {
        for (GamePlayer gp : players) {
            if (gp.getId() == playerId) {
                gp.setStatus(PlayerStatus.WAIT);
                gp.setCurrentBet(0);
            }
        }
    }

    @Override
    public void betPlayer(long playerId, int bet) {
        for (GamePlayer gp : players) {
            if (gp.getId() == playerId) {
                gp.bet(bet);
                gp.setStatus(PlayerStatus.WAIT);
            }
        }
    }

    @Override
    public String toString() {
        return "THTable{id " + id + ", deck size " + deck.getSize()
            + ", community cards " + communityCards + ", " + pot
            + ", Players{" + playersInfo() + ", dealer idx " + dealerIndex
            + ", small blind idx " + smallBlindIndex + ", big blind idx " + bigBlindIndex
            + ", active player idx " + activePlayerIndex + ", min raise " + minRaise
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

    private void setBlindByIndex(int blindIdx, int blind) {
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
        dealerIndex++;
        if (dealerIndex >= players.size()) {
            dealerIndex = 0;
        }
        dealerId = players.get(dealerIndex).getId();
    }

    private void defineBlindIndices() {
        smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex >= players.size()) {
            smallBlindIndex = 0;
        }

        bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex >= players.size()) {
            bigBlindIndex = 0;
        }
    }

    private void defineActivePlayer() {
        activePlayerIndex = bigBlindIndex + 1;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
        }

        GamePlayer activePlayer = players.get(activePlayerIndex);
        activePlayer.setStatus(PlayerStatus.ACTIVE);

        activePlayerId = activePlayer.getId();
    }

    private void seatPlayer(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == Util.DEFAULT_LONG_VALUE) {
                playersSeats[i] = playerId;
                break;
            }
        }
    }

    private void releaseSeat(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == playerId) {
                playersSeats[i] = Util.DEFAULT_LONG_VALUE;
                break;
            }
        }
    }
}
