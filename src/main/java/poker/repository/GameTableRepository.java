package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.GameTable;

import java.util.List;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, Long> {
    List<GameTable> findAllGamesByOrderByIdAsc();
}
