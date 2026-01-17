package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poker.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String mail);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
