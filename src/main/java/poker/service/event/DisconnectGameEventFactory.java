package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.model.event.GameEvent;
import poker.model.event.GameEventData;

import java.sql.Timestamp;

@Component
@Log4j2
public class DisconnectGameEventFactory implements GameEventFactory {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.DISCONNECT;
    }

    @Override
    public GameEvent create(GameTable gameTable, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(gameTable.getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .playerStatus(PlayerStatus.NOT_IN_GAME.getIntStatus()) // todo: think how to handle accidental disconnects
            .actionType(pad.getPlayerAction().getType())
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameEventData.getGameId())
            .userId(gameEventData.getUserId())
            .playerId(gameEventData.getPlayerId())
            .potId(gameTable.getPot().getId())
            .type(gameEventData.getActionType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(gameEventData.getDateTimeMs()))
            .build();
    }
}
