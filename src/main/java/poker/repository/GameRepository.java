package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.Game;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query(value = "SELECT g FROM Game g WHERE g.status != :status ORDER BY g.id ASC")
    List<Game> findAllNotEndedGames(@Param("status") int gameStatus);

    @Query("SELECT g FROM Game g WHERE g.id = :gameId")
    Game findGameById(@Param("gameId") long gameId);
}
