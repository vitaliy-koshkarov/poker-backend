package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.model.PlayerBet;
import poker.repository.PlayerBetRepository;

import java.util.List;

@Service("PlayerBetService")
@Log4j2
@RequiredArgsConstructor
public class PlayerBetService {
    private final PlayerBetRepository playerBetRepo;

    @Transactional(rollbackFor = Exception.class)
    public void createPlayersBets(List<PlayerBet> playersBets) {
        List<PlayerBet> newPlayersBets = playerBetRepo.saveAllAndFlush(playersBets);
        log.info("Created players bets {}", newPlayersBets);
    }
}
