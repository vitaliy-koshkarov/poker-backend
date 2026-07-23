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

import java.util.*;

@Getter
@Setter
@Log4j2
public class THTable implements GameTable {
    private final long id;
    private final String name;
    private final long creatorPlayerId;
    private final int maxPlayers;
    private final int buyIn;

    private GameStatus gameStatus;

    private long dealerId;
    private int dealerIndex;

    private long activePlayerId;

    private int smallBlind;
    private long smallBlindPlayerId;

    private int bigBlind;
    private long bigBlindPlayerId;

    private int minRaise;

    private GamePot pot;

    /**
     * Key - {@link GamePlayer#getId()}, value - {@link GamePlayer}
     */
    private Map<Long, GamePlayer> playersMap;

    private Deck deck;

    private List<Card> communityCards;

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
        this.playersMap = new HashMap<>();
        this.playersSeats = new long[maxPlayers];
    }

    @Override
    public List<GamePlayer> getPlayers() {
        return new ArrayList<>(playersMap.values());
    }

    @Override
    public GamePlayer getActivePlayer() {
        return playersMap.get(activePlayerId);
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
        playersMap.put(gamePlayer.getId(), gamePlayer);
        seatPlayer(gamePlayer.getId());
    }

    @Override
    public void removePlayer(long playerId) {
        playersMap.remove(playerId);
        releaseSeat(playerId);
    }

    @Override
    public void defineNewActivePlayer() {
        int currentActivePlayerIdx = 0;
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == activePlayerId) {
                currentActivePlayerIdx = i;
            }
        }
        currentActivePlayerIdx = currentActivePlayerIdx + 1;
        if (currentActivePlayerIdx >= playersSeats.length) {
            currentActivePlayerIdx = 0;
        }

        activePlayerId = playersSeats[currentActivePlayerIdx];
        playersMap.get(activePlayerId).setStatus(PlayerStatus.ACTIVE);
    }

    @Override
    public void updateGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public void dealStartHands() {
        for (int i = 0; i < 2; i++) {
            for (GamePlayer player : playersMap.values()) {
                player.getCards().add(deck.dealCard());
            }
        }
    }

    @Override
    public void startGame() {
        gameStatus = GameStatus.PRE_FLOP;

        for (GamePlayer player : playersMap.values()) {
            player.refresh();
            player.setChips(buyIn); // fixme: do this only for the very first round
        }

        defineDealerAndBlindAndActivePlayers();
        betBlinds();

//        TODO: calculate min raise every new game
        minRaise = bigBlind;

        deck.shuffle();
        dealStartHands();
    }

    @Override
    public void foldPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(PlayerStatus.FOLD);
        player.setCurrentBet(Util.ZERO_INT);
    }

    @Override
    public void betBlinds() {
        betPlayerBlind(smallBlindPlayerId, smallBlind);
        betPlayerBlind(bigBlindPlayerId, bigBlind);
    }

    @Override
    public void checkPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(PlayerStatus.WAIT);
        player.setCurrentBet(0);
    }

    @Override
    public void betPlayer(long playerId, int bet) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(PlayerStatus.WAIT);
        player.bet(bet);
    }

    @Override
    public String toString() {
        return "THTable{" +
            "id=" + id + ", name=" + name + ", creatorPlayerId=" + creatorPlayerId +
            ", maxPlayers=" + maxPlayers + ", buyIn=" + buyIn + ", gameStatus=" + gameStatus +
            ", dealerId=" + dealerId + ", dealerIndex=" + dealerIndex + ", activePlayerId=" + activePlayerId +
            ", smallBlindPlayerId=" + smallBlindPlayerId + ", bigBlindPlayerId=" + bigBlindPlayerId +
            ", smallBlind=" + smallBlind + ", bigBlind=" + bigBlind + ", minRaise=" + minRaise +
            ", pot=" + pot +
            ", players=" + playersInfo() +
            ", deck=" + deck +
            ", communityCards=" + communityCards +
            ", playersSeats=" + Arrays.toString(playersSeats) +
            '}';
    }

    public void setUpNewRound() {
        gameStatus = GameStatus.PRE_FLOP;

        pot.refresh();
        communityCards.clear();
        for (GamePlayer player : playersMap.values()) {
            player.refresh();
        }

        defineDealerAndBlindAndActivePlayers();
        betBlinds();

//        TODO: calculate min raise every new round
        minRaise = bigBlind;

        deck.shuffle();
        dealStartHands();
    }

    private void betPlayerBlind(long playerId, int blind) {
        GamePlayer player = playersMap.get(playerId);
        player.bet(blind);
        pot.addPlayerBet(player, blind);
    }

    private String playersInfo() {
        var sb = new StringBuilder();
        sb.append("amount: ").append(playersMap.size()).append(", ");
        for (GamePlayer p : playersMap.values()) {
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

    private void defineDealerAndBlindAndActivePlayers() {
        dealerIndex = dealerIndex + 1;
        if (dealerIndex >= playersSeats.length) {
            dealerIndex = 0;
        }
        dealerId = playersSeats[dealerIndex];

        int smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex >= playersSeats.length) {
            smallBlindIndex = 0;
        }
        smallBlindPlayerId = playersSeats[smallBlindIndex];

        int bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex >= playersSeats.length) {
            bigBlindIndex = 0;
        }
        bigBlindPlayerId = playersSeats[bigBlindIndex];

        int activePlayerIndex = bigBlindIndex + 1;
        if (activePlayerIndex >= playersSeats.length) {
            activePlayerIndex = 0;
        }

        activePlayerId = playersSeats[activePlayerIndex];
        playersMap.get(activePlayerId).setStatus(PlayerStatus.ACTIVE);
    }

    private void seatPlayer(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == Util.ZERO_LONG) {
                playersSeats[i] = playerId;
                break;
            }
        }
    }

    private void releaseSeat(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == playerId) {
                playersSeats[i] = Util.ZERO_LONG;
                break;
            }
        }
    }
}
