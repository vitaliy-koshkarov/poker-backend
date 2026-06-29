package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.GamePlayer;
import poker.core.game.GameStatus;
import poker.core.player.PlayerActionData;
import poker.model.PlayerBet;
import poker.service.GameService;
import poker.service.PlayerBetService;
import poker.service.PlayerService;

import java.sql.Timestamp;
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
        List<GamePlayer> gamePlayers = gameEngine.getTable().getPlayers();

        List<PlayerBet> playersBets = new LinkedList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            playersBets.add(
                PlayerBet.builder()
                    .potId(gameEngine.getTable().getPot().getId())
                    .playerId(gamePlayer.getId())
                    .playerBet(gamePlayer.getCurrentBet())
                    .build()
            );
        }
        playerBetService.createPlayersBets(playersBets);

        long dealerId = gameEngine.getTable().getDealerId();
        long activePlayerId = gameEngine.getTable().getActivePlayerId();

        gameService.startGame(gameId, dealerId, activePlayerId,
            GameStatus.PRE_FLOP, new Timestamp(System.currentTimeMillis()));

        for (GamePlayer gPlayer : gamePlayers) {
            playerService.updatePlayerStatusAndChips(gPlayer.getId(), gPlayer.getChips(), gPlayer.getStatus());
        }

        log.info("Player id {} {} game id {}",
            pad.getPlayerDetails().getPlayer().getId(), pad.getPlayerAction(), gameId);

        return true;
    }
}
