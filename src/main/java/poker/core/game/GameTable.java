package poker.core.game;

import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;

import java.util.List;

public interface GameTable {
    long getId();
    String getName();
    long getCreatorPlayerId();
    int getBuyIn();
    GameStatus getGameStatus();
    long getDealerId();
    int getDealerIndex();
    long getActivePlayerId();
    int getActivePlayerIndex();
    int getMaxPlayers();
    int getSmallBlind();
    int getSmallBlindIndex();
    int getBigBlind();
    int getBigBlindIndex();
    int getMinRaise();
    GamePot getPot();
    List<GamePlayer> getPlayers();
    List<GamePlayer> getActivePlayers();
    Deck getDeck();
    List<Card> getCommunityCards();
    long[] getPlayersSeats();
    int getPlayerSeatNumber(long playerId);

    void setGameStatus(GameStatus gameStatus);
    void setDealerId(long dealerId);
    void setDealerIndex(int dealerIndex);
    void setActivePlayerId(long activePlayerId);
    void setActivePlayerIndex(int activePlayerIndex);
    void setSmallBlind(int smallBlind);
    void setSmallBlindIndex(int smallBlindIndex);
    void setBigBlind(int bigBlind);
    void setBigBlindIndex(int bigBlindIndex);
    void setMinRaise(int minRaise);
    void setPot(GamePot pot);
    void setPlayers(List<GamePlayer> players);
    void setDeck(Deck deck);
    void setCommunityCards(List<Card> communityCards);
    void setPlayersSeats(long[] playersSeats);

    void addPlayer(GamePlayer gamePlayer);
    void removePlayer(long playerId);
    void overrideActivePlayer();
    void updateGameStatus(GameStatus gameStatus);
    void startGame();
    void foldPlayer(long playerId);
    void betBlinds();
    void dealStartHands();
}
