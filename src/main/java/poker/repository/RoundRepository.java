package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poker.model.Round;

public interface RoundRepository extends JpaRepository<Round, Long> {
}
