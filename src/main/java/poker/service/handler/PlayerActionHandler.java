package poker.service.handler;

import poker.game.GameEngine;
import poker.model.Game;

public interface PlayerActionHandler {
    void handleAction(GameEngine gameEngine, Game game, Long playerId);
}
