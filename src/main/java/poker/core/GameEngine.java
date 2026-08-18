package poker.core;

import poker.core.game.GameState;
import poker.core.player.PlayerActionData;

public interface GameEngine extends Snapshot<GameState>, Rollback<GameState> {
    GameState getGameState();

    void handlePlayerAction(PlayerActionData pad);
}
