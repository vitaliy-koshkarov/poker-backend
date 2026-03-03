package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.PlayerTable;

import java.util.Set;

@Repository
public interface PlayerTableRepository extends JpaRepository<PlayerTable, Long> {

    @Query("SELECT pt FROM PlayerTable pt WHERE pt.userId = :userId AND pt.playerId = :playerId")
    @Transactional(readOnly = true)
    PlayerTable findPlayerTableByUserAndPlayerIds(@Param("userId") Long userId, @Param("playerId") long playerId);

    @Query("UPDATE PlayerTable pt SET pt.tableIds = :tableIds WHERE pt.id = :playerTableId")
    @Modifying
    @Transactional
    void updatePlayerTable(@Param("playerTableId") Long playerTableId, @Param("tableIds") Set<Long> tableIds);
}
