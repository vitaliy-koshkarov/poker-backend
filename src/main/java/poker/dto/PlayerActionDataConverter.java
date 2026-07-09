package poker.dto;

import poker.core.game.texasholdem.THPlayerActionData;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.PlayerDetails;

public class PlayerActionDataConverter {
//    TODO: Do not use PlayerDetails in engine and DB layer
    public static PlayerActionData convert(long gameId, PlayerActionRequest request,
                                           PlayerDetails playerDetails, PlayerAction playerAction) {
        return THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(playerAction)
            .playerDetails(playerDetails)
            .dateTimeMs(System.currentTimeMillis())
            .build();
    }

    public static PlayerActionData convert(long gameId, PlayerDetails playerDetails, PlayerAction playerAction) {
        return THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(playerAction)
            .playerDetails(playerDetails)
            .dateTimeMs(System.currentTimeMillis())
            .build();
    }
}
