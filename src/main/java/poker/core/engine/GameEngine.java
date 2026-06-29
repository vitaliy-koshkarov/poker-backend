package poker.core.engine;

import poker.core.game.GameState;
import poker.core.game.GameTable;
import poker.core.player.PlayerActionData;

public interface GameEngine {
    GameTable getTable();

    GameState getCurrentGameState();

    void handlePlayerAction(PlayerActionData pad);

    void rollback(GameState snapshot);
}
