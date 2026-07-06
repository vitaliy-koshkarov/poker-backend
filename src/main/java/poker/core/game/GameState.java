package poker.core.game;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;

import java.util.List;

@Builder
@Getter
@ToString
public class GameState {
    private long gameId;
    private String name;
    private long creatorPlayerId;
    private GameStatus gameStatus;
    private long dealerId;
    private int dealerIndex;
    private long activePlayerId;
    private int activePlayerIndex;
    private int maxPlayers;
    private int smallBlind;
    private int smallBlindIndex;
    private int bigBlind;
    private int bigBlindIndex;
    private int minRaise;
    private int buyIn;
    private GamePot gamePot;
    private List<GamePlayer> gamePlayers;
    private Deck deck;
    private List<Card> communityCards;
    private long[] playersSeats;
}
