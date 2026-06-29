package poker.service.handler;

import poker.core.engine.GameEngine;

public interface DBPlayerActionHandler {
    boolean handleAction(long playerId, GameEngine gameEngine);
}
