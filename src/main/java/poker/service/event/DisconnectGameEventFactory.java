package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
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
    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(engine.table().getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .playerStatus(engine.table().getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .actionType(pad.getPlayerAction().getType())
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameEventData.getGameId())
            .userId(gameEventData.getUserId())
            .playerId(gameEventData.getPlayerId())
            .potId(engine.table().getPot().getId())
            .type(gameEventData.getActionType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(gameEventData.getDateTimeMs()))
            .build();
    }
}
