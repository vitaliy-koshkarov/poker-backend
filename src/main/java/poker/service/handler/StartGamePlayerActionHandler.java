package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.PlayerAction;
import poker.game.texasholdem.THEngine;
import poker.game.texasholdem.THPlayer;
import poker.model.Game;
import poker.model.GameStatus;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.service.PlayerService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("StartGamePlayerActionHandler")
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartGamePlayerActionHandler implements PlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;

    @Override
    public void handleAction(GameEngine gameEngine, Game game, PlayerDetails playerDetails) {
        engineHandling(gameEngine, game);

        repositoryHandling(gameEngine, game, playerDetails.getPlayer().getId());
    }

    private void engineHandling(GameEngine gameEngine, Game game) {
//        gameEngine.getTable().setUpNewRound();
        log.info("{} new round, game id {}", PlayerAction.START_GAME.getActionName(), game.getId());
//        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(GameEngine gameEngine, Game game, long playerId) {
        game.setStatus(GameStatus.PRE_FLOP.getStatus());
        game.setStartedAt(new Timestamp(System.currentTimeMillis()));

        long dealerId = ((THEngine) gameEngine).getDealerId();
        game.setDealerId(dealerId);

        long activePlayerId = ((THEngine) gameEngine).getActivePlayerId();
        game.setActivePlayerId(activePlayerId);
        gameService.updateGame(game);

        List<THPlayer> thPlayers = Collections.emptyList(); // gameEngine.getTable().getPlayers();
        List<Long> playerIds = new ArrayList<>();
        for (THPlayer thPlayer : thPlayers) {
            playerIds.add(thPlayer.getId());
        }
        var players = playerService.getPlayersByIds(playerIds);
        for (Player player : players) {
            for (THPlayer thPlayer : thPlayers) {
                if (thPlayer.getId() == player.getId()) {
                    player.setStatus(thPlayer.getStatus().getStatus());
                }
            }
        }
        playerService.updatePlayers(players);

        log.info("Player id {} {} game id {}", playerId, PlayerAction.START_GAME.getActionName(), game.getId());
    }
}
