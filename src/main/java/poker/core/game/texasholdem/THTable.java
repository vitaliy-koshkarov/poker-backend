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
import poker.util.Util;

import java.util.*;

import static poker.core.game.GameStatus.*;
import static poker.core.player.PlayerStatus.*;

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

    /**
     * Index is a player seat number
     */
    private long[] playersSeats;

    private Deck deck;

    private List<Card> communityCards;

    private THRound bettingRound;

    public THTable(long gameId, String name, long creatorPlayerId, int maxPlayers, int buyIn,
                   GameStatus gameStatus, int smallBlind, int bigBlind, GamePot pot, long roundId) {
        this.id = gameId;
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
        bettingRound = new THRound(roundId, gameId, Util.ZERO_INT, Util.ZERO_INT);
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
        return Util.INVALID_INT_VALUE;
    }

    @Override
    public GamePlayer getPlayerById(long playerId) {
        return playersMap.get(playerId);
    }

    @Override
    public int getLastMaxBet() {
        return bettingRound.getLastMaxBet();
    }

    @Override
    public THRound getRound() {
        return bettingRound;
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
    public void startGame() {
//        todo: shuffle player seats
        refreshTable();

        for (GamePlayer player : playersMap.values()) {
            player.setChips(buyIn);
        }

        addAllPlayersToAct();

        defineDealerAndBlindAndActivePlayers();

        betBlinds();

        updateLastAggressor(bigBlindPlayerId, bigBlind);

        defineMinRaise();

        deck.shuffle();

        dealStartHands();

        gameStatus = PRE_FLOP;
    }

    @Override
    public void foldPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(FOLD);
        player.setCurrentBet(Util.ZERO_INT);
        bettingRound.removePlayerToAct(playerId);

        defineNewActivePlayer();
        defineMinRaise();
    }

    @Override
    public void checkPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(CHECK);
        player.setCurrentBet(Util.ZERO_INT);
        bettingRound.removePlayerToAct(playerId);

        defineNewActivePlayer();
        defineMinRaise();
    }

    @Override
    public void betPlayer(long playerId, int bet) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(bet == player.getChips() ? ALL_IN : BET);
        player.bet(bet);

        pot.addPlayerBet(activePlayerId, bet);

        updatePlayersToAct(playerId);

        if (bet > bettingRound.getLastMaxBet()) {
            updateLastAggressor(activePlayerId, bet);
        }

        defineNewActivePlayer();
        defineMinRaise();
    }

    @Override
    public void preFlop() {
        preFlopStage();
    }

    @Override
    public void flop() {
        flopStage();
    }

    @Override
    public void turn() {
        turnStage();
    }

    @Override
    public void river() {
        riverStage();
    }

    @Override
    public void showdown() {
        showdownStage();
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

    private void dealStartHands() {
        for (int i = 0; i < 2; i++) {
            for (GamePlayer player : playersMap.values()) {
                player.getCards().add(deck.dealCard());
            }
        }
    }

    private void betBlinds() {
        betPlayerBlind(smallBlindPlayerId, smallBlind);
        betPlayerBlind(bigBlindPlayerId, bigBlind);
    }

    private void betPlayerBlind(long playerId, int blind) {
        playersMap.get(playerId).bet(blind);
        pot.addPlayerBet(playerId, blind);
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
//        todo: add random dealerId calculation
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
        playersMap.get(activePlayerId).setStatus(ACTIVE);
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

    private void defineNewActivePlayer() {
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
        playersMap.get(activePlayerId).setStatus(ACTIVE);
    }

    private void addAllPlayersToAct() {
        bettingRound.getPlayersToAct().clear();
        for (GamePlayer gamePlayer : playersMap.values()) {
            bettingRound.addPlayersToAct(gamePlayer.getId());
        }
    }

    private void updateLastAggressor(long playerId, int bet) {
        bettingRound.setLastAggressorPlayerId(playerId);
        bettingRound.setLastMaxBet(bet);
    }

    private void defineMinRaise() {
        if (bettingRound.getLastMaxBet() > bigBlind) {
            if (getActivePlayer().getChips() - bettingRound.getLastMaxBet() >= 0) {
                minRaise = bettingRound.getLastMaxBet();
            } else {
                minRaise = getActivePlayer().getChips();
            }
        } else { // lastMaxBet == BB
            minRaise = Math.min(getActivePlayer().getChips(), bigBlind);
        }
    }

    private void updatePlayersToAct(long playerId) {
        bettingRound.getPlayersToAct().clear();

        for (GamePlayer p : playersMap.values()) {
            if (p.getId() != playerId && !FOLD.equals(p.getStatus()) && !ALL_IN.equals(p.getStatus())) {
                bettingRound.addPlayersToAct(p.getId());
            }
        }
    }

    private void refreshTable() {
        for (GamePlayer player : playersMap.values()) {
            player.refresh();
        }
        pot.refresh();
        communityCards.clear();
    }

    private void preFlopStage() {
        refreshTable();

        addAllPlayersToAct();

        defineDealerAndBlindAndActivePlayers();

        betBlinds();

        updateLastAggressor(bigBlindPlayerId, bigBlind);

        defineMinRaise();

        deck.shuffle();

        dealStartHands();

        gameStatus = PRE_FLOP;
    }

    private void flopStage() {
        gameStatus = FLOP;

        bettingRound.getPlayersToAct().clear();

        for (GamePlayer p : playersMap.values()) {
            if (!FOLD.equals(p.getStatus()) && !ALL_IN.equals(p.getStatus())) {
                bettingRound.addPlayersToAct(p.getId());
            }
        }

        // todo: determine whose move it is

        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.dealCard());
        }
    }

    private void turnStage() {
        gameStatus = TURN;

        communityCards.add(deck.dealCard());

        // todo: determine whose move it is
    }

    private void riverStage() {
        gameStatus = RIVER;

        communityCards.add(deck.dealCard());

        // todo: determine whose move it is
    }

    private void showdownStage() {
        preFlopStage();
    }
}
