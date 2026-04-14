package poker.game;

import poker.model.Game;
import poker.model.Player;
import poker.model.event.GameEvent;

import java.util.List;

public interface GameEngine {
    GameEvent handleAction(PlayerAction action, long playerId, Game game, List<Player> players);

    GameState getGameState();
}
