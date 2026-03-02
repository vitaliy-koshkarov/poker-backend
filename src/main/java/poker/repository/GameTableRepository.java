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

    @Modifying
    @Query(value = "UPDATE GameTable AS gt SET gt.name = :name WHERE gt.id = :id")
    @Transactional
    void updateGameTableNameById(@Param("id") Long id, @Param("name") String name);

    @Query("SELECT gt FROM GameTable gt WHERE gt.id = :tableId")
    @Transactional(readOnly = true)
    GameTable getGameTableById(@Param("tableId") Long tableId);

    @Modifying
    @Query("UPDATE GameTable gt SET gt.currentPlayers = :currentPlayersIds WHERE gt.id = :gameId")
    @Transactional
    void addPlayerToGame(@Param("gameId") Long gameId,
                         @Param("currentPlayersIds") Set<Long> currentPlayersIds);

    @Query("SELECT gt FROM GameTable gt WHERE gt.id = :gameId")
    @Transactional(readOnly = true)
    GameTable findGameById(@Param("gameId") long gameId);
}
