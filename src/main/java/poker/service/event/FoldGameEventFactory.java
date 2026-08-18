package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GameTable;
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
    public GameEvent create(GameTable gameTable, PlayerActionData pad) {
        var gameEventData = GameEventData.builder()
            .gameId(gameTable.getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .gameStatus(gameTable.getGameStatus().getIntStatus())
            .playerStatus(gameTable.getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .actionType(pad.getPlayerAction().getType())
            .roundNumber(gameTable.getRound().getRoundNumber())
            .lastAggressorPlayerId(gameTable.getRound().getLastAggressorPlayerId())
            .lastMaxBet(gameTable.getRound().getLastMaxBet())
            .playersToAct(gameTable.getRound().getPlayersToAct())
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameEventData.getGameId())
            .userId(gameEventData.getUserId())
            .playerId(gameEventData.getPlayerId())
            .potId(gameTable.getPot().getId())
            .type(pad.getPlayerAction().getType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(pad.getDateTimeMs()))
            .build();
    }
}
