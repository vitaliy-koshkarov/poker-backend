package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.Game;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query(value = "SELECT g FROM Game g WHERE g.status != :status ORDER BY g.id ASC")
    List<Game> findAllNotEndedGames(@Param("status") int gameStatus);

    @Query("SELECT g FROM Game g WHERE g.id = :gameId")
    Game findGameById(@Param("gameId") long gameId);

    @Modifying
    @Query("""
        UPDATE Game g
        SET g.dealerId = :dealerId,
            g.activePlayerId = :activePlayerId,
            g.status = :status,
            g.startedAt = :startedAt
            WHERE g.id = :gameId
        """)
    void startGame(@Param("gameId") long gameId,
                   @Param("dealerId") long dealerId,
                   @Param("activePlayerId") long activePlayerId,
                   @Param("status") int status,
                   @Param("startedAt") Timestamp startedAt
    );

    @Modifying
    @Query("UPDATE Game g SET g.activePlayerId = :activePlayerId WHERE g.id = :gameId")
    void updateActivePlayerId(@Param("gameId") long gameId, @Param("activePlayerId") long activePlayerId);
}
