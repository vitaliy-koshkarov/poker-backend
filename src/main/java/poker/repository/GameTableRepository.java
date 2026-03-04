package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.GameTable;

import java.util.List;
import java.util.Set;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, Long> {
    @Transactional(readOnly = true)
    List<GameTable> findAllGamesByOrderByIdAsc();

    @Query("SELECT gt FROM GameTable gt WHERE gt.id = :gameTableId")
    @Transactional(readOnly = true)
    GameTable findGameTableById(@Param("gameTableId") Long tableId);

    @Query(value = "UPDATE GameTable AS gt SET gt.name = :name WHERE gt.id = :gameTableId")
    @Modifying
    @Transactional
    void updateGameTableName(@Param("gameTableId") Long gameTableId, @Param("name") String name);

    @Query("UPDATE GameTable gt SET gt.currentPlayers = :currentPlayersIds WHERE gt.id = :gameId")
    @Modifying
    @Transactional
    void updateCurrentPlayers(@Param("gameId") Long gameId,
                              @Param("currentPlayersIds") Set<Long> currentPlayersIds);
}
