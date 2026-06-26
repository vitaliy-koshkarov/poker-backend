package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.model.Game;
import poker.model.PlayerDetails;
import poker.core.player.PlayerStatus;
import poker.service.GameSeatService;
import poker.service.PlayerService;

@Component("DisconnectPlayerActionHandler")
@Log4j2
@RequiredArgsConstructor
@ToString
public class DisconnectPlayerActionHandler implements PlayerActionHandler {
    private final PlayerService playerService;
    private final GameSeatService gameSeatService;

    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        long gameId = game.getId();
        engineHandling(gameEngine, playerDetails.getPlayer().getId(), gameId);

        repositoryHandling(playerDetails, gameId);
    }

    private void engineHandling(GameEngine gameEngine, long playerId, long gameId) {
//        var thTable = gameEngine.getTable();
//        thTable.removePlayer(playerId);
        log.info("Player id {} {} game id {}", playerId, PlayerAction.DISCONNECT.getActionName(), gameId);
//        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(PlayerDetails playerDetails, long gameId) {
        var player = playerDetails.getPlayer();
        long playerId = player.getId();
        player.setStatus(PlayerStatus.NOT_IN_GAME.getIntStatus());
        playerService.updatePlayer(player);
        gameSeatService.releaseGameSeat(playerDetails.getUser().getId(), playerId, gameId);
        log.info("Player id {} {} from game id {}", playerId, PlayerAction.DISCONNECT.getActionName(), gameId);
    }
}
