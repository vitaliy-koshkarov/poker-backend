package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.GameTable;

import java.util.List;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, Long> {

    @Modifying
    @Query("""
            DELETE FROM GameTable gt
            WHERE gt.userId = :userId AND gt.playerId = :playerId AND gt.gameId = :gameId
        """)
    void removeGameTableByUserIdAndPlayerIdAndGameId(@Param("userId") long userId,
                                                     @Param("playerId") long playerId,
                                                     @Param("gameId") long gameId);

    @Query("SELECT gt FROM GameTable gt WHERE gt.gameId = :gameId")
    List<GameTable> findAllGameTablesByGameId(@Param("gameId") long gameId);

    @Query("SELECT gt FROM GameTable gt WHERE gt.gameId = :gameId AND gt.playerId = :playerId")
    GameTable findGameTableByGameIdAndPlayerId(@Param("gameId") long gameId, @Param("playerId") long playerId);

    @Modifying
    @Query("DELETE FROM GameTable gt WHERE gt.gameId = :gameId")
    void removeGameTableByGameId(@Param("gameId") long gameId);
}
