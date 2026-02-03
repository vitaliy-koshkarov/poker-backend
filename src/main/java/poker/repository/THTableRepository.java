package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.THTable;

@Repository
public interface THTableRepository extends JpaRepository<THTable, Long> {
}
