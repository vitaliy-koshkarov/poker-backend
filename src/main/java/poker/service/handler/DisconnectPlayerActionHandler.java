package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerStatus;
import poker.service.GameSeatService;
import poker.service.PlayerService;
import poker.service.UserService;

@Component("DISCONNECT")
@Log4j2
@RequiredArgsConstructor
@ToString
public class DisconnectPlayerActionHandler implements DBPlayerActionHandler {
    private final PlayerService playerService;
    private final UserService userService;
    private final GameSeatService gameSeatService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(long playerId, GameEngine gameEngine) {
        long gameId = gameEngine.getTable().getId();
        var player = playerService.getPlayerById(playerId);
        var user = userService.getUserByPlayerId(playerId);

        player.setStatus(PlayerStatus.NOT_IN_GAME.getIntStatus());
        playerService.updatePlayer(player);
        gameSeatService.releaseGameSeat(user.getId(), playerId, gameId);

        log.info("Player id {} {} from game id {}", playerId, PlayerAction.DISCONNECT.getActionName(), gameId);

        return true;
    }

    private void engineHandling(GameEngine gameEngine, long playerId, long gameId) {
//        var thTable = gameEngine.getTable();
//        thTable.removePlayer(playerId);
        log.info("Player id {} {} game id {}", playerId, PlayerAction.DISCONNECT.getActionName(), gameId);
//        log.info("{}", gameEngine.getTable());
    }
}
