package poker.service.event;

import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.event.GameEvent;

public interface GameEventFactory {
    PlayerAction supportsPlayerAction();

    GameEvent create(GameEngine engine, PlayerActionData pad);
}
