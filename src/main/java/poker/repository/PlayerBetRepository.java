package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.PlayerBet;

@Repository
public interface PlayerBetRepository extends JpaRepository<PlayerBet, Long> {

    @Modifying
    @Query("DELETE FROM PlayerBet pb WHERE pb.potId = :potId")
    void removeAllPlayersBetsByPotId(@Param("potId") long potId);
}
