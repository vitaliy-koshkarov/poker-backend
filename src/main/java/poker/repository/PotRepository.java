package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.Pot;

@Repository
public interface PotRepository extends JpaRepository<Pot, Long> {

    @Query("SELECT p FROM Pot p WHERE p.id = :potId")
    Pot getPotById(@Param("potId") Long potId);
}
