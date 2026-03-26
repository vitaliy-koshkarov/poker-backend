package poker.game;

import poker.model.Game;
import poker.model.Player;
import poker.model.Pot;
import poker.model.event.GameEvent;

import java.util.List;

public interface GameEngine {
    GameEvent handleAction(Game game, Long playerId, Long dealerId, Long activePlayerId,
                           List<Player> players, List<Object> communityCards, PlayerAction action, Pot pot);

    GameState getGameState();
}
