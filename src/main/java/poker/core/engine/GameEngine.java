package poker.core.engine;

import poker.core.game.GameState;
import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

public interface GameEngine {
    long getActivePlayerId();

    GameTable getTable();

    GameState getCurrentGameState();

    void handlePlayerAction(long playerId, PlayerActionData playerActionData, PlayerAction playerAction);
}
