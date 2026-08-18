package poker.service.handler;

import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

public interface DBPlayerActionHandler {
    PlayerAction supportsPlayerAction();

    boolean handleAction(GameTable gameTable, PlayerActionData pad);
}
