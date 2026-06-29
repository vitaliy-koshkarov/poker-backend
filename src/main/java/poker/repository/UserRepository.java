package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :userEmail")
    User findUserByEmail(@Param("userEmail") String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findUserById(@Param("userId") long userId);

    @Query("SELECT u FROM User u WHERE u.playerId = :playerId")
    User findUserByPlayerId(@Param("playerId") long playerId);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") long userId, @Param("password") String password);
}
