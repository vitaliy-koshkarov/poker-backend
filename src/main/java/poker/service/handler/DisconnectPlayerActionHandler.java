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
import poker.service.PlayerSeatService;
import poker.service.PlayerService;
import poker.service.UserService;

@Component("DISCONNECT")
@Log4j2
@RequiredArgsConstructor
@ToString
public class DisconnectPlayerActionHandler implements DBPlayerActionHandler {
    private final PlayerService playerService;
    private final UserService userService;
    private final PlayerSeatService playerSeatService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = pad.getGameId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        playerService.updatePlayerStatus(playerId, PlayerStatus.NOT_IN_GAME);
        playerSeatService.releasePlayerSeat(pad.getPlayerDetails().getUser().getId(), playerId, gameId);

        log.info("Player id {} {} from game id {}", playerId, PlayerAction.DISCONNECT.getActionName(), gameId);

        return true;
    }
}
