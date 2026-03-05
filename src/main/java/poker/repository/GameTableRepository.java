package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.GameTable;

import java.util.Collection;
import java.util.List;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, Long> {

    @Query("SELECT gt FROM GameTable gt WHERE gt.userId = :userId AND gt.playerId = :playerId")
    @Transactional(readOnly = true)
    Collection<GameTable> findGameTablesByUserIdAndPlayerId(@Param("userId") Long userId,
                                                            @Param("playerId") long playerId);

    @Query("DELETE FROM GameTable gt WHERE gt.userId = :userId AND gt.playerId = :playerId AND gt.gameId = :gameId")
    @Modifying
    @Transactional
    void removeGameTableByUserIdAndPlayerIdAndGameId(@Param("userId") Long userId,
                                                     @Param("playerId") Long playerId,
                                                     @Param("gameId") Long gameId);

    @Query("SELECT gt FROM GameTable gt WHERE gt.gameId = :gameId")
    @Transactional(readOnly = true)
    List<GameTable> findAllGameTablesById(@Param("gameId") Long gameId);
}
