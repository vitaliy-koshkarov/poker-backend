package poker.game;

import poker.game.texasholdem.THTable;
import poker.model.event.GameEventData;

public interface GameEngine {
    THTable getTable();

    GameEventData getGameEventData();
}
