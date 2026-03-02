package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.User;
import poker.repository.UserRepository;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User createUser(User user) {
        var newUser = userRepo.save(user);
        log.info("User created {}", newUser);
        return newUser;
    }

    public User getUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    public User getUserPlayerById(Long id) {
        return userRepo.findUserPlayerById(id);
    }

    public User getUserById(Long userId) {
        return userRepo.findUserById(userId);
    }

    public void updateUserPassword(long userId, String password) {
        userRepo.updatePassword(userId, password);
        log.info("Password updated user id {}", userId);
    }
}
