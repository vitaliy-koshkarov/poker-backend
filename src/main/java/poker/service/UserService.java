package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poker.model.Role;
import poker.model.User;
import poker.repository.UserRepository;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User createUser(String email, String password, Long playerId) {
        var user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(Role.ROLE_USER)
            .playerId(playerId)
            .build();

        var newUser = userRepo.save(user);
        log.info("User created {}", newUser);
        return newUser;
    }

    public User getUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    public User getUserById(Long userId) {
        return userRepo.findUserById(userId);
    }

    public void updateUserPassword(long userId, String password) {
        userRepo.updatePassword(userId, password);
        log.info("Password updated user id {}", userId);
    }
}
