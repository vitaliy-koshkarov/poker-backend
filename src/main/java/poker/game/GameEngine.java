package poker.game;

import poker.model.Game;
import poker.model.Player;
import poker.model.event.GameEventData;

import java.util.List;

public interface GameEngine {
    void handleAction(PlayerAction action, long playerId, Game game, List<Player> players);

    GameEventData getGameEventData();
}
