package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.model.PlayerStatus;
import poker.service.GameTableService;
import poker.service.PlayerService;

@Component(value = PlayerActions.DISCONNECT)
@Log4j2
@ToString
public class DisconnectPlayerActionHandler implements PlayerActionHandler {
    private final PlayerService playerService;
    private final GameTableService gameTableService;

    @Autowired
    public DisconnectPlayerActionHandler(PlayerService playerService, GameTableService gameTableService) {
        this.playerService = playerService;
        this.gameTableService = gameTableService;
    }

    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        long gameId = game.getId();
        engineHandling(gameEngine, playerDetails.getPlayer().getId(), gameId);

        repositoryHandling(playerDetails, gameId);
    }

    private void engineHandling(GameEngine gameEngine, long playerId, long gameId) {
        var thTable = gameEngine.getTable();
        thTable.removePlayer(playerId);
        log.info("Player id {} {} game id {}", playerId, PlayerActions.DISCONNECT, gameId);
        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(PlayerDetails playerDetails, long gameId) {
        var player = playerDetails.getPlayer();
        long playerId = player.getId();
        player.setStatus(PlayerStatus.NOT_IN_GAME.getStatus());
        playerService.updatePlayer(player);
        gameTableService.removePlayerFromGameTable(playerDetails.getUser().getId(), playerId, gameId);
        log.info("Player id {} {} from game id {}", playerId, PlayerActions.DISCONNECT, gameId);
    }
}
