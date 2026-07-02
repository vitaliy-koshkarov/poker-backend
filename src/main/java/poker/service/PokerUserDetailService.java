package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.model.PlayerDetails;
import poker.repository.PlayerRepository;
import poker.repository.UserRepository;

@Service
@Log4j2
@RequiredArgsConstructor
public class PokerUserDetailService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PlayerRepository playerRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        var user = userRepo.findUserByEmail(email);
        var player = playerRepo.findPlayerByUserId(user.getId());

        var playerDetails = new PlayerDetails(user, player);
        log.debug("Load user {} by email {}", playerDetails, email);
        return playerDetails;
    }

    @Transactional(readOnly = true)
    public UserDetails getUserById(long userId) {
        var user = userRepo.findUserById(userId);
        var player = playerRepo.findPlayerByUserId(userId);

        var playerDetails = new PlayerDetails(user, player);
        log.debug("Load user {} by id {}", playerDetails, userId);
        return playerDetails;
    }
}
