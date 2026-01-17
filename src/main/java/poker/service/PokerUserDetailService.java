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
    private final UserRepository userRepository;

    public PokerUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not found user by email " + email));

        log.info("Auth by email {}", email);

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getNickname())
            .password(user.getPassword())
            .roles(user.getRole().name().replace("ROLE_", ""))
            .build();
    }
}
