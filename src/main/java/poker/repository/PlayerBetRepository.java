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

    @Modifying
    @Query("UPDATE PlayerBet pb SET pb.playerBet = :bet WHERE pb.potId = :potId AND pb.playerId = :playerId")
    void updateBet(@Param("playerId") long playerID, @Param("potId") long potId, @Param("bet") int bet);
}
