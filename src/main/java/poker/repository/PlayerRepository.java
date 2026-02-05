package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poker.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByNickname(String nickname);
}
