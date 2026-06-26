package poker.core.engine;

import poker.core.game.GameState;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

public interface GameEngine {
    GameState getCurrentGameState();

    void handlePlayerAction(long playerId, PlayerActionData playerActionData, PlayerAction playerAction);
}
