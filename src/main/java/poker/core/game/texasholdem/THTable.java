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
import static poker.util.Util.*;

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
     * Index is a player seat number. Value is {@link GamePlayer#getId()}
     */
    private long[] playersSeats;

    private THRound bettingRound;

    private Deck deck;

    private List<Card> communityCards;

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
        bettingRound = new THRound(roundId, gameId, INT_ZERO, INT_ZERO);
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
    public void startNewRound() {
//        todo: shuffle player seats before new game (before 1 round only)
        refreshTable();

        defineDealerAndBlindAndActivePlayers();

        updatePlayersToActForNewRound();

        betBlinds();

        determineMinRaise();

        deck.shuffle();

        dealStartHands();

        gameStatus = PRE_FLOP;
    }

    @Override
    public void foldPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(FOLD);
        player.setCurrentBet(INT_ZERO);
        bettingRound.removePlayerToAct(playerId);

        determineNewActivePlayer();

        determineMinRaise();
    }

    @Override
    public void checkPlayer(long playerId) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(CHECK);
        player.setCurrentBet(INT_ZERO);
        bettingRound.removePlayerToAct(playerId);

        determineNewActivePlayer();
        determineMinRaise();
    }

    @Override
    public void call(long playerId, int playerBet) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(CALL);
        player.bet(playerBet);

        pot.addPlayerBet(playerId, playerBet);

        bettingRound.removePlayerToAct(playerId);

        determineNewActivePlayer();

        determineMinRaise();
    }

    @Override
    public void betPlayer(long playerId, int playerBet) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(playerBet == player.getChips() ? ALL_IN : BET);

        bettingRound.removePlayerToAct(playerId);

        int newPlayerBet = playersMap.get(playerId).getCurrentBet() + playerBet;
        if (newPlayerBet > bettingRound.getLastMaxBet()) {
            updateLastAggressor(playerId, newPlayerBet);
        }
        player.bet(playerBet);

        pot.addPlayerBet(playerId, playerBet);

        updatePlayersToActExceptAggressor(playerId);

        determineNewActivePlayer();

        determineMinRaise();
    }

    @Override
    public void raise(long playerId, int playerBet) {
        GamePlayer player = playersMap.get(playerId);
        player.setStatus(RAISE);

        bettingRound.removePlayerToAct(playerId);

        int newPlayerBet = playersMap.get(playerId).getCurrentBet() + playerBet;
        if (newPlayerBet > bettingRound.getLastMaxBet()) {
            updateLastAggressor(playerId, newPlayerBet);
        }
        player.bet(playerBet);

        pot.addPlayerBet(playerId, playerBet);

        updatePlayersToActExceptAggressor(playerId);

        determineNewActivePlayer();

        determineMinRaise();
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
        betPlayerBlind(smallBlindPlayerId, Math.min(playersMap.get(smallBlindPlayerId).getChips(), smallBlind));
        if (playersMap.get(smallBlindPlayerId).getCurrentBet() > bettingRound.getLastMaxBet()) {
            updateLastAggressor(smallBlindPlayerId, playersMap.get(smallBlindPlayerId).getCurrentBet());
        }

        betPlayerBlind(bigBlindPlayerId, Math.min(playersMap.get(bigBlindPlayerId).getChips(), bigBlind));
        if (playersMap.get(bigBlindPlayerId).getCurrentBet() > bettingRound.getLastMaxBet()) {
            updateLastAggressor(bigBlindPlayerId, playersMap.get(bigBlindPlayerId).getCurrentBet());
        }
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
        dealerIndex = dealerIndex + INT_ONE;
        if (dealerIndex >= playersSeats.length) {
            dealerIndex = INT_ZERO;
        }
        dealerId = playersSeats[dealerIndex];

        int smallBlindIndex = dealerIndex + INT_ONE;
        if (smallBlindIndex >= playersSeats.length) {
            smallBlindIndex = INT_ZERO;
        }
        smallBlindPlayerId = playersSeats[smallBlindIndex];

        int bigBlindIndex = smallBlindIndex + INT_ONE;
        if (bigBlindIndex >= playersSeats.length) {
            bigBlindIndex = INT_ZERO;
        }
        bigBlindPlayerId = playersSeats[bigBlindIndex];

        int activePlayerIndex = bigBlindIndex + INT_ONE;
        if (activePlayerIndex >= playersSeats.length) {
            activePlayerIndex = INT_ZERO;
        }

//        todo: if player always fold or all-in, then choose next available player
        activePlayerId = playersSeats[activePlayerIndex];
        playersMap.get(activePlayerId).setStatus(ACTIVE);
    }

    private void seatPlayer(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == Util.LONG_ZERO) {
                playersSeats[i] = playerId;
                break;
            }
        }
    }

    private void releaseSeat(long playerId) {
        for (int i = 0; i < playersSeats.length; i++) {
            if (playersSeats[i] == playerId) {
                playersSeats[i] = Util.LONG_ZERO;
                break;
            }
        }
    }

    private void updateLastAggressor(long playerId, int bet) {
        bettingRound.setLastAggressorPlayerId(playerId);
        bettingRound.setLastMaxBet(bet);
    }

    private void updatePlayersToActForNewRound() {
        bettingRound.getPlayersToAct().clear();

        if (dealerIndex != INT_ZERO && dealerIndex + INT_ONE < playersSeats.length) {
//            add players after dealer
            addRemainingPlayersToAct(dealerIndex + INT_ONE, playersSeats.length);
//            add players before dealer
            addRemainingPlayersToAct(INT_ZERO, dealerIndex);
        } else {
//            add all players
            addRemainingPlayersToAct(INT_ZERO, playersSeats.length);
        }
    }

    private void updatePlayersToActExceptAggressor(long aggressorPlayerId) {
        bettingRound.getPlayersToAct().clear();

        int aggressorPlayerSeatIdx = getPlayerSeatNumber(aggressorPlayerId);
        if (aggressorPlayerSeatIdx + INT_ONE < playersSeats.length) {
//            add players after aggressor
            addRemainingPlayersToAct(aggressorPlayerSeatIdx + INT_ONE, playersSeats.length);
//            add players before aggressor
            addRemainingPlayersToAct(INT_ZERO, aggressorPlayerSeatIdx);
        } else {
//            aggressor is the last. Add players before aggressor
            addRemainingPlayersToAct(INT_ZERO, playersSeats.length - INT_ONE);
        }
    }

    private void addRemainingPlayersToAct(int startIdx, int endIdx) {
        for (int i = startIdx; i < endIdx; i++) {
            if (!(FOLD.equals(playersMap.get(playersSeats[i]).getStatus())
                || ALL_IN.equals(playersMap.get(playersSeats[i]).getStatus()))) {
                bettingRound.addPlayersToAct(playersSeats[i]);
            }
        }
    }

    private void determineNewActivePlayer() {
        if (!bettingRound.getPlayersToAct().isEmpty()) {
            activePlayerId = bettingRound.getPlayersToAct().getFirst();

            playersMap.get(activePlayerId).setStatus(ACTIVE);
        }
    }

    private void determineMinRaise() {
        int diff = Math.abs(playersMap.get(activePlayerId).getCurrentBet() - bettingRound.getLastMaxBet());

        if (diff == 0) {
            minRaise = Math.min(playersMap.get(activePlayerId).getChips(), bigBlind);
        } else {
            minRaise = Math.min(playersMap.get(activePlayerId).getChips(), diff);
        }
    }

    private void refreshTable() {
        for (GamePlayer player : playersMap.values()) {
            player.refresh();
        }
        pot.refresh();
        bettingRound.refresh();
        bettingRound.increment();
        communityCards.clear();
    }

    private void preFlopStage() {
        refreshTable();

        defineDealerAndBlindAndActivePlayers();

        betBlinds();

        updatePlayersToActForNewRound();

        determineMinRaise();

        deck.shuffle();

        dealStartHands();

        gameStatus = PRE_FLOP;
    }

    private void flopStage() {
        refreshGameForNewStage(3, FLOP);
    }

    private void turnStage() {
        refreshGameForNewStage(INT_ONE, TURN);
    }

    private void riverStage() {
        refreshGameForNewStage(INT_ONE, RIVER);
    }

    private void showdownStage() {
        preFlopStage();
    }

    private void refreshGameForNewStage(int dealCardsAmount, GameStatus gameStatus) {
        for (GamePlayer p : playersMap.values()) {
            p.setStatus(WAIT);
            p.setCurrentBet(INT_ZERO);
        }

        pot.clearPlayerBets();

        bettingRound.refresh();

//        Next player who can move is the next 'active' player after dealer
        updatePlayersToActForNewRound();

        for (int i = 0; i < dealCardsAmount; i++) {
            communityCards.add(deck.dealCard());
        }

        determineNewActivePlayer();

        this.gameStatus = gameStatus;
    }
}
