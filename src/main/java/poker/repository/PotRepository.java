package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.Pot;

@Repository
public interface PotRepository extends JpaRepository<Pot, Long> {

    @Query("SELECT p FROM Pot p WHERE p.id = :potId")
    Pot getPotById(@Param("potId") long potId);

    @Modifying
    @Query("UPDATE Pot p SET p.total = :total WHERE p.id = :id")
    void updateTotal(@Param("id") long id, @Param("total") int total);
}
