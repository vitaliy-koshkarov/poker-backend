package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.Game;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Transactional(readOnly = true)
    List<Game> findAllGamesByOrderByIdAsc();

    @Query("SELECT g FROM Game g WHERE g.id = :gameId")
    @Transactional(readOnly = true)
    Game findGameById(@Param("gameId") Long gameId);

    @Query(value = "UPDATE Game AS g SET g.status = :gameStatus WHERE g.id = :gameId")
    @Modifying
    @Transactional
    void updateGameStatus(Long gameId, Integer gameStatus);
}
