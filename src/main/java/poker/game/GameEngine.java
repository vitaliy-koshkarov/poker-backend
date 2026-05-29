package poker.game;

import poker.model.Game;
import poker.model.event.GameEventData;

public interface GameEngine {
    void handleAction(PlayerAction action, long playerId, Game game);

    GameEventData getGameEventData();
}
