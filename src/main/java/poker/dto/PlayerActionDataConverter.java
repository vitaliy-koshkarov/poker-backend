package poker.dto;

import poker.core.game.texasholdem.THPlayerActionData;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.PlayerDetails;

public class PlayerActionDataConverter {

    public static PlayerActionData convert(long gameId, PlayerActionRequest request,
                                           PlayerDetails playerDetails, PlayerAction playerAction) {
        return THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(playerAction)
            .userId(playerDetails.getUser().getId())
            .playerId(playerDetails.getPlayer().getId())
            .nickname(playerDetails.getPlayer().getNickname())
            .chips(playerDetails.getPlayer().getChips())
            .playerBet(playerDetails.getPlayer().getCurrentBet())
            .dateTimeMs(System.currentTimeMillis())
            .build();
    }

    public static PlayerActionData forStartGameAndDisconnect(long gameId, PlayerDetails playerDetails, PlayerAction playerAction) {
        return THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(playerAction)
            .userId(playerDetails.getUser().getId())
            .playerId(playerDetails.getPlayer().getId())
            .dateTimeMs(System.currentTimeMillis())
            .build();
    }
}
