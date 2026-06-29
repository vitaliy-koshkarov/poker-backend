package poker.service.handler;

import poker.core.engine.GameEngine;
import poker.core.player.PlayerActionData;

public interface DBPlayerActionHandler {
    boolean handleAction(GameEngine gameEngine, PlayerActionData pad);
}
