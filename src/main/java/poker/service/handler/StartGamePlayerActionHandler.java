package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.game.texasholdem.THEngine;
import poker.game.texasholdem.THPlayer;
import poker.model.Game;
import poker.model.GameStatus;
import poker.model.Player;
import poker.service.GameService;
import poker.service.PlayerService;

import java.sql.Timestamp;
import java.util.List;

@Component(value = PlayerActions.START_GAME)
@Log4j2
@ToString
public class StartGamePlayerActionHandler implements PlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;

    @Autowired
    public StartGamePlayerActionHandler(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @Override
    public void handleAction(GameEngine gameEngine, Game game, Player actionInitiatorPlayer, List<Player> players) {
        engineHandling(gameEngine, game);

        repositoryHandling(gameEngine, game, actionInitiatorPlayer, players);
    }

    private void engineHandling(GameEngine gameEngine, Game game) {
        gameEngine.getTable().setUpNewRound();
        log.info("{} new round, game id {}", PlayerActions.START_GAME, game.getId());
        log.info("{}", gameEngine.getTable());
    }

    private void repositoryHandling(GameEngine gameEngine, Game game, Player actionInitiatorPlayer, List<Player> players) {
        game.setStatus(GameStatus.PRE_FLOP.getStatus());
        game.setStartedAt(new Timestamp(System.currentTimeMillis()));

        long dealerId = ((THEngine) gameEngine).getDealerId();
        game.setDealerId(dealerId);

        long activePlayerId = ((THEngine) gameEngine).getActivePlayerId();
        game.setActivePlayerId(activePlayerId);
        gameService.updateGame(game);

        List<THPlayer> thPlayers = gameEngine.getTable().getPlayers();
        for (Player player : players) {
            for (THPlayer thPlayer : thPlayers) {
                if (thPlayer.getId() == player.getId()) {
                    player.setStatus(thPlayer.getStatus().getStatus());
                }
            }
        }
        playerService.updatePlayers(players);

        log.info("Player id {} {} game id {}", actionInitiatorPlayer.getId(), PlayerActions.START_GAME, game.getId());
    }
}
