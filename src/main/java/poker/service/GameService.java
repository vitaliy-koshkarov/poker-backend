package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.config.GameProps;
import poker.core.engine.GameEngineRegistry;
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
    private final GameEngineRegistry gameEngineRegistry;
    private final GameRepository gameRepo;
    private final PotService potService;
    private final PlayerBetService playerBetService;
    private final PlayerService playerService;
    private final PlayerSeatService playerSeatService;
    private final RoundService roundService;

    @Transactional(rollbackFor = Exception.class)
    public void createGame(long creatorPlayerId, CreateGameRequest createGameRequest) {
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
            .dealerId(Util.LONG_ZERO)
            .activePlayerId(Util.LONG_ZERO)
            .build();

        var newGame = gameRepo.save(game);

        long roundId = roundService.createRound(game.getId());

        log.info("Created {}, player id {}", newGame, creatorPlayerId);

        gameEngineRegistry.registerNewGame(game, roundId);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeGame(long gameId) {
//        TODO: check correct game deletion
        var game = gameRepo.findGameById(gameId);
        long potId = game.getPotId();

        playerSeatService.deletePlayerSeatByIdGameId(gameId);

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
        log.debug("Game id {} started at {}", gameId, startedAt);
    }

    @Transactional(readOnly = true)
    public List<Game> getListNonEndedGames() {
        return gameRepo.findAllNotEndedGames(GameStatus.END.getIntStatus());
    }

    public void updateActivePlayer(long gameId, long activePlayerId) {
        gameRepo.updateActivePlayerId(gameId, activePlayerId);
    }
}
