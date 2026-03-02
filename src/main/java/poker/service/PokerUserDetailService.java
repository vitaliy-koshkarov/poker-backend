package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.model.User;
import poker.repository.PlayerRepository;
import poker.repository.UserRepository;

@Service
@Log4j2
public class PokerUserDetailService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PlayerRepository playerRepo;

    public PokerUserDetailService(UserRepository userRepository,
                                  PlayerRepository playerRepo) {
        this.userRepo = userRepository;
        this.playerRepo = playerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findUserByEmail(email);
        Player player = playerRepo.findPlayerByUserId(user.getId());

        var playerDetails = new PlayerDetails(user, player);
        log.debug("Load user {} by email {}", playerDetails, email);
        return playerDetails;
    }

    public UserDetails findUserById(Long userId) {
        var user = userRepo.findUserById(userId);
        var player = playerRepo.findPlayerByUserId(userId);

        var playerDetails = new PlayerDetails(user, player);
        log.debug("Load user {} by id {}", playerDetails, userId);
        return playerDetails;
    }
}
