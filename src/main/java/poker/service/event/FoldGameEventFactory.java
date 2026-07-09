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
public class FoldGameEventFactory implements GameEventFactory {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.FOLD;
    }

    @Override
    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(engine.getTable().getId())
            .userId(pad.getPlayerDetails().getUser().getId())
            .playerId(pad.getPlayerDetails().getPlayer().getId())
            .playerStatus(EventUtil.getPlayerStatus(
                engine.getTable().getPlayers(),
                pad.getPlayerDetails().getPlayer().getId())
            )
            .actionType(pad.getPlayerAction().getType())
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameEventData.getGameId())
            .userId(gameEventData.getUserId())
            .playerId(gameEventData.getPlayerId())
            .potId(engine.getTable().getPot().getId())
            .type(pad.getPlayerAction().getType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(pad.getDateTimeMs()))
            .build();
    }
}
