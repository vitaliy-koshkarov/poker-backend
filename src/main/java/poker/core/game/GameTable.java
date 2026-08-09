package poker.core.game;

import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.game.texasholdem.THRound;
import poker.core.player.GamePlayer;

import java.util.List;
import java.util.Map;

// TODO: refactoring
public interface GameTable {
    long getId();
    String getName();
    long getCreatorPlayerId();
    int getBuyIn();
    GameStatus getGameStatus();
    long getDealerId();
    int getDealerIndex();
    long getActivePlayerId();
    int getMaxPlayers();
    int getSmallBlind();
    long getSmallBlindPlayerId();
    int getBigBlind();
    long getBigBlindPlayerId();
    int getLastMaxBet();
    int getMinRaise();
    GamePot getPot();
    List<GamePlayer> getPlayers();
    GamePlayer getActivePlayer();
    Deck getDeck();
    List<Card> getCommunityCards();
    long[] getPlayersSeats();
    int getPlayerSeatNumber(long playerId);
    GamePlayer getPlayerById(long playerId);
    THRound getRound();

    void setGameStatus(GameStatus gameStatus);
    void setDealerId(long dealerId);
    void setDealerIndex(int dealerIndex);
    void setActivePlayerId(long activePlayerId);
    void setSmallBlind(int smallBlind);
    void setSmallBlindPlayerId(long smallBlindPlayerId);
    void setBigBlind(int bigBlind);
    void setBigBlindPlayerId(long bigBlindPlayerId);
    void setMinRaise(int minRaise);
    void setPot(GamePot pot);
    void setPlayersMap(Map<Long, GamePlayer> players);
    void setDeck(Deck deck);
    void setCommunityCards(List<Card> communityCards);
    void setPlayersSeats(long[] playersSeats);
    void setBettingRound(THRound bettingRound);

    void addPlayer(GamePlayer gamePlayer);
    void removePlayer(long playerId);
    void defineNewActivePlayer();
    void betBlinds();
    void defineMinRaise();
    void dealStartHands();

    void startGame();
    void foldPlayer(long playerId);
    void checkPlayer(long playerId);
    void betPlayer(long playerId, int bet);
}
