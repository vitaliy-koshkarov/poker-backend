package poker.service.handler;

import poker.core.engine.GameEngine;
import poker.model.Game;
import poker.model.PlayerDetails;

public interface PlayerActionHandler {
    void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails);
}
