package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poker.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByNickname(String nickname);

    @Query("SELECT p FROM Player p JOIN User u ON u.playerId = p.id WHERE u.id = :userId")
    Player findPlayerByUserId(@Param("userId") long userId);

    @Modifying
    @Query("UPDATE Player p SET p.nickname = :nickname WHERE p.id = :playerId")
    void updatePlayerNickname(@Param("playerId") long playerId, @Param("nickname") String nickname);

    @Modifying
    @Query("UPDATE Player p SET p.status = :status WHERE p.id = :playerId")
    void updateStatus(@Param("playerId") long playerId, @Param("status") int status);

    @Modifying
    @Query("UPDATE Player p SET p.status = :status, p.chips = :chips WHERE p.id = :playerId")
    void updatePlayerStatusAndChips(@Param("playerId") long playerId, @Param("chips") int chips, @Param("status") int playerStatus);

    @Modifying
    @Query("UPDATE Player p SET p.status = :status, p.currentBet = :bet WHERE p.id = :playerId")
    void updateStatusAndCurrentBet(@Param("playerId") long playerId, @Param("status") int status, @Param("bet") int bet);
}
