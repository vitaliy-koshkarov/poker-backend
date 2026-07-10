package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.PlayerBet;
import poker.repository.PlayerBetRepository;

import java.util.List;

@Service("PlayerBetService")
@Log4j2
@RequiredArgsConstructor
public class PlayerBetService {
    private final PlayerBetRepository playerBetRepo;

    public void createPlayersBets(List<PlayerBet> playersBets) {
        List<PlayerBet> newPlayersBets = playerBetRepo.saveAllAndFlush(playersBets);
        log.debug("Created players bets {}", newPlayersBets);
    }

    public void deletePlayersBets(long potId) {
        playerBetRepo.removeAllPlayersBetsByPotId(potId);
        log.info("Players' bets removed, pot id {}", potId);
    }

    public void updatePlayerBet(long playerId, long potId, int bet) {
        playerBetRepo.updateBet(playerId, potId, bet);
        log.debug("Player id {} pot id {} bet {}", playerId, potId, bet);
    }
}
