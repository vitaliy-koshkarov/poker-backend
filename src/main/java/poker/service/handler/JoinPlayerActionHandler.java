package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.game.GameTable;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.service.GameEventService;
import poker.service.PlayerSeatService;
import poker.service.PlayerService;

@Component
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements DBPlayerActionHandler {
    private final PlayerSeatService playerSeatService;
    private final PlayerService playerService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.JOIN_GAME;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameTable gameTable, PlayerActionData pad) {
        long gameId = pad.getGameId();
        long userId = pad.getUserId();
        long playerId = pad.getPlayerId();

        int playerSeatNumber = gameTable.getPlayerSeatNumber(playerId);
        long playerSeatId = playerSeatService.createPlayerSeat(userId, playerId, gameId, playerSeatNumber);

        long eventId = gameEventService.createAndSaveEvent(gameTable, pad);

        log.info("Player id {} {} seat id {} game id {} event id {}",
            playerId, pad.getPlayerAction(), playerSeatId, gameId, eventId);

        return true;
    }
}
