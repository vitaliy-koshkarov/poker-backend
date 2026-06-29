package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.game.GameStatus;
import poker.core.player.PlayerActionData;
import poker.model.Player;
import poker.model.PlayerBet;
import poker.service.GameService;
import poker.service.PlayerBetService;
import poker.service.PlayerService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component("START")
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartGamePlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
        var game = gameService.getGameById(gameId);
        List<GamePlayer> gamePlayers = gameEngine.getTable().getPlayers();

        List<PlayerBet> playersBets = new LinkedList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            playersBets.add(
                PlayerBet.builder()
                    .potId(game.getPotId())
                    .playerId(gamePlayer.getId())
                    .playerBet(gamePlayer.getCurrentBet())
                    .build()
            );
        }
        playerBetService.createPlayersBets(playersBets);

        game.setStatus(GameStatus.PRE_FLOP.getIntStatus());
        game.setStartedAt(new Timestamp(System.currentTimeMillis()));

        long dealerId = gameEngine.getTable().getDealerId();
        game.setDealerId(dealerId);

        long activePlayerId = gameEngine.getTable().getActivePlayerId();
        game.setActivePlayerId(activePlayerId);
        gameService.updateGame(game);

        List<Long> playerIds = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            playerIds.add(gamePlayer.getId());
        }
        var players = playerService.getPlayersByIds(playerIds);
        for (Player player : players) {
            for (GamePlayer gamePlayer : gamePlayers) {
                if (gamePlayer.getId() == player.getId()) {
                    player.setStatus(gamePlayer.getStatus().getIntStatus());
                }
            }
        }
        playerService.updatePlayers(players);

        log.info("Player id {} {} game id {}",
            pad.getPlayerDetails().getPlayer().getId(), PlayerAction.START_GAME.getActionName(), game.getId());

        return true;
    }
}
