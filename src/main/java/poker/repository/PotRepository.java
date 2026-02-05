package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.Pot;

@Repository
public interface PotRepository extends JpaRepository<Pot, Long> {
}
