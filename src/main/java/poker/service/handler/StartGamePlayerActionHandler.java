package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.GamePlayer;
import poker.core.game.GameStatus;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.PlayerBet;
import poker.service.GameEventService;
import poker.service.GameService;
import poker.service.PlayerBetService;
import poker.service.PlayerService;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartGamePlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.START_GAME;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
        long dealerId = gameEngine.getTable().getDealerId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();
        long activePlayerId = gameEngine.getTable().getActivePlayerId();

        gameService.startGame(gameId, dealerId, activePlayerId,
            GameStatus.PRE_FLOP, new Timestamp(pad.getDateTimeMs()));

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

        for (GamePlayer gPlayer : gamePlayers) {
            playerService.updatePlayerStatusAndChips(gPlayer.getId(), gPlayer.getChips(), gPlayer.getStatus());
        }

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} game id {} status {} event id {}",
            playerId, pad.getPlayerAction(), gameId, gameEngine.getTable().getGameStatus(), eventId);

        return true;
    }
}
