package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :userEmail")
    @Transactional(readOnly = true)
    User findUserByEmail(@Param("userEmail") String email);

    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    @Transactional(readOnly = true)
    User findUserById(@Param("userId") Long id);

    @Query("SELECT u, p FROM User u JOIN Player p ON u.playerId = p.id WHERE u.id = :userId")
    @Transactional(readOnly = true)
    User findUserPlayerById(@Param("userId") Long id);

    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    @Modifying
    @Transactional
    void updatePassword(@Param("userId") long userId, @Param("password") String password);
}
