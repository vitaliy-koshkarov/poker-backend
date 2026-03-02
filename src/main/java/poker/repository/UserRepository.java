package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poker.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String mail);

    boolean existsByEmail(String email);

    @Query("SELECT u, p FROM User u JOIN Player p ON u.player.id = p.id WHERE u.id = :userId")
    User getUserById(@Param("userId") Long id);
}
