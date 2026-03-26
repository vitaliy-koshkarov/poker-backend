package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.Player;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByNickname(String nickname);

    @Query("UPDATE Player p SET p.status = :playerStatus WHERE p.id = :playerId")
    @Modifying
    @Transactional
    void updatePlayerStatus(Long playerId, Integer playerStatus);

    @Query("SELECT p FROM Player p JOIN User u ON u.playerId = p.id WHERE u.id = :userId")
    @Transactional(readOnly = true)
    Player findPlayerByUserId(@Param("userId") long userId);

    @Query("UPDATE Player p SET p.nickname = :nickname WHERE p.id = :playerId")
    @Modifying
    @Transactional
    void updatePlayerNickname(@Param("playerId") long playerId, @Param("nickname") String nickname);

    @Query("UPDATE Player p SET p.chips = :chips, p.status = :status WHERE p.id IN (:playerIdList)")
    @Modifying
    @Transactional
    void startGameForPlayers(List<Long> playerIdList, Integer status, Integer chips);
}
