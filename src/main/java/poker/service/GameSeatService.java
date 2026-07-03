package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.GameSeat;
import poker.repository.GameSeatRepository;

import java.sql.Timestamp;
import java.util.List;

@Service("GameSeatService")
@Log4j2
@RequiredArgsConstructor
public class GameSeatService {
    private final GameSeatRepository gameSeatRepo;

    public GameSeat createGameSeat(long userId, long playerId, long gameId) {
        var gameSeat = GameSeat.builder()
            .userId(userId)
            .playerId(playerId)
            .gameId(gameId)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        var newGameSeat = gameSeatRepo.save(gameSeat);
        log.info("Created game seat {}", newGameSeat);
        return newGameSeat;
    }

    public void releaseGameSeat(long userId, long playerId, long gameId) {
        gameSeatRepo.removeGameSeatByUserIdAndPlayerIdAndGameId(userId, playerId, gameId);
        log.info("Removed game seat, user id {}, player id {}, game id {}", userId, playerId, gameId);
    }

    public List<GameSeat> getGameSeatsByGameId(long gameId) {
        return gameSeatRepo.findAllGameSeatsByGameId(gameId);
    }

    public void deleteGameSeatByIdGameId(long gameId) {
        gameSeatRepo.removeGameSeatByGameId(gameId);
    }
}
