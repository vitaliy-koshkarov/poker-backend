package poker.service.handler;

import poker.game.GameEngine;
import poker.model.Game;
import poker.model.Player;

import java.util.List;

public interface PlayerActionHandler {
    void handleAction(GameEngine gameEngine, Game game, Player actionInitiatorPlayer, List<Player> players);
}
