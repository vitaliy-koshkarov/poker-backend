package poker.game;

import poker.game.playeraction.PlayerAction;
import poker.model.Game;
import poker.model.Player;
import poker.model.event.GameEventData;

public interface GameEngine {
    void handleAction(PlayerAction action, Game game, Player player);

    GameEventData getGameEventData();
}
