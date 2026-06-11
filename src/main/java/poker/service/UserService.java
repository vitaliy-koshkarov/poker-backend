package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.model.Role;
import poker.model.User;
import poker.repository.UserRepository;

import java.sql.Timestamp;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PlayerService playerService;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, PlayerService playerService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.playerService = playerService;
    }

    @Transactional(readOnly = true)
    public boolean isUserExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public User createUser(String email, String password, String nickname, Timestamp now) {
        var player = playerService.createPlayer(nickname, now);

        var user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(Role.ROLE_USER)
            .playerId(player.getId())
            .createdAt(now)
            .build();

        var newUser = userRepo.save(user);
        log.info("User created {}", newUser);
        return newUser;
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(long userId, String password) {
        userRepo.updatePassword(userId, password);
        log.info("Password updated user id {}", userId);
    }
}
