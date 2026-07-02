package poker.core.engine;

import poker.core.Rollback;
import poker.core.Snapshot;
import poker.core.game.GameState;
import poker.core.game.GameTable;
import poker.core.player.PlayerActionData;

public interface GameEngine extends Snapshot<GameState>, Rollback<GameState> {
    GameTable getTable();

    GameState getGameState();

    void handlePlayerAction(PlayerActionData pad);
}
