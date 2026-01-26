package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poker.model.User;
import poker.repository.UserRepository;

@Service
@Log4j2
public class PokerUserDetailService implements UserDetailsService {
    private final UserRepository userRepo;

    public PokerUserDetailService(UserRepository userRepository) {
        this.userRepo = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> {
                log.error("User not found with email {}", email);
                return new UsernameNotFoundException("Not found user by email " + email);
            });

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name().replace("ROLE_", ""))
            .build();
    }

    public UserDetails findUserById(Long userId) throws UsernameNotFoundException {
        var user = userRepo.findById(userId)
            .orElseThrow(() -> {
                log.error("User not found with id {}", userId);
                return new UsernameNotFoundException("Not found user by id " + userId);
            });

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name().replace("ROLE_", ""))
            .build();
    }
}
