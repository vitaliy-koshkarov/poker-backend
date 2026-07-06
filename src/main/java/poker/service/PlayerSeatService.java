package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.PlayerSeat;
import poker.repository.PlayerSeatRepository;

import java.sql.Timestamp;

@Service("PlayerSeatService")
@Log4j2
@RequiredArgsConstructor
public class PlayerSeatService {
    private final PlayerSeatRepository playerSeatRepo;

    public PlayerSeat createPlayerSeat(long userId, long playerId, long gameId, int seatNumber) {
        var playerSeat = PlayerSeat.builder()
            .userId(userId)
            .playerId(playerId)
            .gameId(gameId)
            .seatNumber(seatNumber)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        var newPlayerSeat = playerSeatRepo.save(playerSeat);
        log.info("Created player seat {}", newPlayerSeat);
        return newPlayerSeat;
    }

    public void releasePlayerSeat(long userId, long playerId, long gameId) {
        playerSeatRepo.removePlayerSeatByUserIdAndPlayerIdAndGameId(userId, playerId, gameId);
        log.info("Removed player seat, user id {}, player id {}, game id {}", userId, playerId, gameId);
    }

    public void deletePlayerSeatByIdGameId(long gameId) {
        playerSeatRepo.removePlayerSeatByGameId(gameId);
        log.info("Removed player seat, game id {}", gameId);
    }
}
