package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.game.GameTable;
import poker.core.player.GamePlayer;
import poker.core.game.GameStatus;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.PlayerBet;
import poker.service.*;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartGamePlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.START_GAME;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameTable gameTable, PlayerActionData pad) {
        long gameId = gameTable.getId();
        long dealerId = gameTable.getDealerId();
        long playerId = pad.getPlayerId();
        long activePlayerId = gameTable.getActivePlayerId();
        long roundId = gameTable.getRound().getId();
        int roundNumber = gameTable.getRound().getRoundNumber();
        long lastAggressorPlayerId = gameTable.getRound().getLastAggressorPlayerId();
        int lastMaxBet = gameTable.getRound().getLastMaxBet();
        List<Long> playersToAct = gameTable.getRound().getPlayersToAct();

        gameService.startGame(gameId, dealerId, activePlayerId,
            GameStatus.PRE_FLOP, new Timestamp(pad.getDateTimeMs()));

        roundService.updateRound(roundId, roundNumber, lastAggressorPlayerId, lastMaxBet, playersToAct);

        List<GamePlayer> gamePlayers = gameTable.getPlayers();

        List<PlayerBet> playersBets = new LinkedList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            playersBets.add(
                PlayerBet.builder()
                    .potId(gameTable.getPot().getId())
                    .playerId(gamePlayer.getId())
                    .playerBet(gamePlayer.getCurrentBet())
                    .build()
            );
        }
        playerBetService.createPlayersBets(playersBets);

        for (GamePlayer gPlayer : gamePlayers) {
            playerService.updatePlayerStatusAndChips(gPlayer.getId(), gPlayer.getChips(), gPlayer.getStatus());
        }

        long eventId = gameEventService.createAndSaveEvent(gameTable, pad);

        log.info("Player id {} {} game id {} status {} event id {}",
            playerId, pad.getPlayerAction(), gameId, gameTable.getGameStatus(), eventId);

        return true;
    }
}
