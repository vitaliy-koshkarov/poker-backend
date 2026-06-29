package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.GameSeat;

import java.util.List;

@Repository
public interface GameSeatRepository extends JpaRepository<GameSeat, Long> {

    @Modifying
    @Query("""
            DELETE FROM GameSeat gs
            WHERE gs.userId = :userId AND gs.playerId = :playerId AND gs.gameId = :gameId
        """)
    void removeGameSeatByUserIdAndPlayerIdAndGameId(@Param("userId") long userId,
                                                    @Param("playerId") long playerId,
                                                    @Param("gameId") long gameId);

    @Query("SELECT gs FROM GameSeat gs WHERE gs.gameId = :gameId")
    List<GameSeat> findAllGameSeatsByGameId(@Param("gameId") long gameId);

    @Query("SELECT gs FROM GameSeat gs WHERE gs.gameId = :gameId AND gs.playerId = :playerId")
    GameSeat findGameSeatByGameIdAndPlayerId(@Param("gameId") long gameId, @Param("playerId") long playerId);

    @Modifying
    @Query("DELETE FROM GameSeat gs WHERE gs.gameId = :gameId")
    void removeGameSeatByGameId(@Param("gameId") long gameId);
}
