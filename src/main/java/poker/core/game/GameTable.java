package poker.core.game;

import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;

import java.util.List;

public interface GameTable {
    long getId();
    String getName();
    long getCreatorPlayerId();
    GameStatus getGameStatus();
    long getDealerId();
    long getActivePlayerId();
    int getCurrentPlayersCount();
    int getMaxPlayers();
    int getSmallBlind();
    int getBigBlind();
    int getMinRaise();
    int getBuyIn();
    GamePot getPot();
    List<GamePlayer> getPlayers();
    List<GamePlayer> getActivePlayers();
    Deck getDeck();
    List<Card> getCommunityCards();

    void addPlayer(GamePlayer gamePlayer);
    void updateGameStatus(GameStatus gameStatus);
    void betBlinds();
    void dealStartHands();
}
