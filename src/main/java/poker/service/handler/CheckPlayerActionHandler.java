package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.service.GameEventService;
import poker.service.GameService;
import poker.service.PlayerService;
import poker.util.Util;

@Component
@RequiredArgsConstructor
@Log4j2
@ToString
public class CheckPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CHECK;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        gameService.updateActivePlayer(gameId, gameEngine.getTable().getActivePlayerId());
        playerService.updatePlayerStatusAndCurrentBet(playerId, PlayerStatus.WAIT, Util.ZERO_INT);

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} status {} game id {} event id {}",
            playerId, pad.getPlayerAction(), PlayerStatus.WAIT, gameId, eventId);

        return true;
    }
}
