package poker.service.event;

import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.event.GameEvent;

public interface GameEventFactory {
    PlayerAction supportsPlayerAction();

    GameEvent create(GameTable gameTable, PlayerActionData pad);
}
