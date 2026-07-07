package poker.core.player;

import poker.model.PlayerDetails;

public interface PlayerActionData {
    long getGameId();
    PlayerAction getPlayerAction();
    PlayerDetails getPlayerDetails();
    long getDateTimeMs();
}
