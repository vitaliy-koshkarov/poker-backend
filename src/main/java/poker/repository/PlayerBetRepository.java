package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.PlayerBet;

@Repository
public interface PlayerBetRepository extends JpaRepository<PlayerBet, Long> {
}
