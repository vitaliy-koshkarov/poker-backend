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

    public User getUserById(Long id) {
        return userRepo.getUserById(id);
    }
}
