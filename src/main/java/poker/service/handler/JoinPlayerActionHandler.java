package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.game.GameStatus;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.model.GameSeat;
import poker.service.GameSeatService;
import poker.service.PlayerService;

@Component("JOIN")
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements DBPlayerActionHandler {
    private final GameSeatService gameSeatService;
    private final PlayerService playerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = pad.getGameId();
        long userId = pad.getPlayerDetails().getUser().getId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();
        int playerChips = pad.getPlayerDetails().getPlayer().getChips();

//        If player join when the game already started, then player's chips do not need to update,
//        because the last value is already stored in the database
        GameStatus gameStatus = gameEngine.getTable().getGameStatus();
        if (GameStatus.WAITING_FOR_PLAYERS.equals(gameStatus)) {
//            update player chips to buyIn and create game seat
            playerChips = gameEngine.getTable().getBuyIn();

            GameSeat gameSeat = gameSeatService.createGameSeat(userId, playerId, gameId);
            log.info("Player id {} {}, game seat id {}", playerId, pad.getPlayerAction(), gameSeat);
        }

        playerService.updatePlayerStatusAndChips(playerId, playerChips, PlayerStatus.JOIN_THE_GAME);

        log.info("Player id {} {}, game id {}", playerId, pad.getPlayerAction(), gameId);

        return true;
    }
}
