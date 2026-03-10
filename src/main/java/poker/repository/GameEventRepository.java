package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.event.GameEvent;

@Repository
public interface GameEventRepository extends JpaRepository<GameEvent, Long> {
}
