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
public class CallGameEventFactory implements GameEventFactory {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CALL;
    }

    @Override
    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(engine.table().getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .potId(engine.table().getPot().getId())
            .gameStatus(engine.table().getGameStatus().getIntStatus())
            .playerStatus(engine.table().getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .actionType(pad.getPlayerAction().getType())
            .currentBet(engine.table().getPlayerById(pad.getPlayerId()).getCurrentBet())
            .roundNumber(engine.table().getRound().getRoundNumber())
            .lastAggressorPlayerId(engine.table().getRound().getLastAggressorPlayerId())
            .lastMaxBet(engine.table().getRound().getLastMaxBet())
            .playersToAct(engine.table().getRound().getPlayersToAct())
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
