package poker.service.handler;

import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

public interface DBPlayerActionHandler {
    PlayerAction supportsPlayerAction();

    boolean handleAction(GameEngine gameEngine, PlayerActionData pad);
}
