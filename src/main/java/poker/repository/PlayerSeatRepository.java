package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.PlayerSeat;

@Repository
public interface PlayerSeatRepository extends JpaRepository<PlayerSeat, Long> {

    @Modifying
    @Query("""
            DELETE FROM PlayerSeat ps
            WHERE ps.userId = :userId AND ps.playerId = :playerId AND ps.gameId = :gameId
        """)
    void removePlayerSeatByUserIdAndPlayerIdAndGameId(@Param("userId") long userId,
                                                      @Param("playerId") long playerId,
                                                      @Param("gameId") long gameId);

    @Modifying
    @Query("DELETE FROM PlayerSeat ps WHERE ps.gameId = :gameId")
    void removePlayerSeatByGameId(@Param("gameId") long gameId);
}
