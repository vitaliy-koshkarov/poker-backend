package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.config.GameProps;
import poker.dto.game.CreateGameRequest;
import poker.core.game.GameStatus;
import poker.model.*;
import poker.repository.GameRepository;
import poker.util.Util;

import java.sql.Timestamp;
import java.util.List;

@Service("GameService")
@Log4j2
@RequiredArgsConstructor
@ToString
public class GameService {
    private final GameProps gameProps;
    private final GameRepository gameRepo;
    private final PotService potService;
    private final PlayerBetService playerBetService;
    private final PlayerService playerService;
    private final GameSeatService gameSeatService;

    @Transactional(rollbackFor = Exception.class)
    public Game createGame(long creatorPlayerId, CreateGameRequest createGameRequest) {
        var pot = potService.createPot();

        var game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .buyIn(createGameRequest.buyIn())
            .smallBlind(gameProps.getSmallBlind())
            .bigBlind(gameProps.getBigBlind())
            .name(createGameRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS.getIntStatus())
            .potId(pot.getId())
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .creatorPlayerId(creatorPlayerId)
            .dealerId(Util.DEFAULT_LONG_VALUE)
            .activePlayerId(Util.DEFAULT_LONG_VALUE)
            .build();

        var newGame = gameRepo.save(game);
        log.info("Created {} by player id {}", newGame, creatorPlayerId);

        return newGame;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeGame(long gameId) {
        var game = gameRepo.findGameById(gameId);
        long potId = game.getPotId();

        gameSeatService.deleteGameSeatByIdGameId(gameId);
        log.info("Removed game table with game id {}", gameId);

        gameRepo.deleteById(gameId);
        log.info("Removed game id {}", gameId);

        potService.deleteById(potId);
        log.info("Removed pot id {}", potId);

        playerBetService.deletePlayersBets(potId);
        log.info("Removed players' bets, pot id {}", potId);

        return true;
    }

    public void startGame(long gameId, long dealerId, long activePlayerId, GameStatus gameStatus, Timestamp startedAt) {
        gameRepo.startGame(gameId, dealerId, activePlayerId, gameStatus.getIntStatus(), startedAt);
        log.info("Game id {} started at {}", gameId, startedAt);
    }

    @Transactional(readOnly = true)
    public List<Game> getListNonEndedGames() {
        return gameRepo.findAllNotEndedGames(GameStatus.END.getIntStatus());
    }
}
