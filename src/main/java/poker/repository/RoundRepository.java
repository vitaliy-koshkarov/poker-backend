package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poker.model.Round;

import java.util.Set;

public interface RoundRepository extends JpaRepository<Round, Long> {

    @Modifying
    @Query("""
        UPDATE Round r
        SET r.roundNumber = :rN, r.lastAggressorPlayerId = :playerId, r.lastMaxBet = :maxBet, r.playersToAct = :players
        WHERE r.id = :id
        """)
    void updateRound(@Param("id") long roundId,
                     @Param("rN") int roundNumber,
                     @Param("playerId") long lastAggressorPlayerId,
                     @Param("maxBet") int lastMaxBet,
                     @Param("players") Set<Long> playersToAct);
}
