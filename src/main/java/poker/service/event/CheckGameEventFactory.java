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
public class CheckGameEventFactory implements GameEventFactory {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CHECK;
    }

    @Override
    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(engine.getTable().getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .gameStatus(engine.getTable().getGameStatus().getIntStatus())
            .playerStatus(engine.getTable().getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .actionType(pad.getPlayerAction().getType())
            .currentBet(engine.getTable().getPlayerById(pad.getPlayerId()).getCurrentBet())
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameEventData.getGameId())
            .userId(gameEventData.getUserId())
            .playerId(gameEventData.getPlayerId())
            .potId(engine.getTable().getPot().getId())
            .type(gameEventData.getActionType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(gameEventData.getDateTimeMs()))
            .build();
    }
}
