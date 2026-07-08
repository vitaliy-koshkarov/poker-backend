package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerActionData;
import poker.service.GameEventService;
import poker.service.PlayerSeatService;
import poker.service.PlayerService;

@Component("JOIN")
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements DBPlayerActionHandler {
    private final PlayerSeatService playerSeatService;
    private final PlayerService playerService;
    private final GameEventService gameEventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = pad.getGameId();
        long userId = pad.getPlayerDetails().getUser().getId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        int playerSeatNumber = gameEngine.getTable().getPlayerSeatNumber(playerId);
        long playerSeatId = playerSeatService.createPlayerSeat(userId, playerId, gameId, playerSeatNumber);
        log.info("Create seat id {} player id {} game id {}", playerSeatId, playerId, gameId);

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);
        log.info("Player id {} {} game id {} event id {}",
            playerId, pad.getPlayerAction().getActionName(), gameId, eventId);

        return true;
    }
}
