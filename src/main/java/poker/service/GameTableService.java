package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.Game;
import poker.model.GameTable;
import poker.model.Player;
import poker.repository.GameTableRepository;

import java.sql.Timestamp;
import java.util.List;

@Service("GameTableService")
@Log4j2
public class GameTableService {
    private final GameTableRepository gameTableRepo;

    public GameTableService(GameTableRepository gameTableRepository) {
        this.gameTableRepo = gameTableRepository;
    }

    public GameTable createGameTable(Long userId, Long playerId, Long gameId) {
        var gameTable = GameTable.builder()
            .userId(userId)
            .playerId(playerId)
            .gameId(gameId)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        var newGameTable = gameTableRepo.save(gameTable);
        log.info("Game table created {}", newGameTable);
        return newGameTable;
    }

    public void removePlayerFromGameTable(Long userId, Long playerId, Long gameId) {
        gameTableRepo.removeGameTableByUserIdAndPlayerIdAndGameId(userId, playerId, gameId);
    }

    public List<GameTable> getGameTablesByGameId(long gameId) {
        return gameTableRepo.findAllGameTablesByGameId(gameId);
    }

    /**
     * @param gameId {@link Game#getId()}
     * @param playerId {@link Player#getId()}
     * @return {@link GameTable} associated with this {@link Game#getId()} and {@link Player#getId()},
     * or null if there is nothing
     */
    public GameTable getGameTableByGameIdAndPlayerId(Long gameId, Long playerId) {
        return gameTableRepo.findGameTableByGameIdAndPlayerId(gameId, playerId);
    }

    public void deleteGameTableByIdGameId(Long gameId) {
        gameTableRepo.removeGameTableByGameId(gameId);
    }
}
