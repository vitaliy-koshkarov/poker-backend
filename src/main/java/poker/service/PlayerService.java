package poker.service;

import common.PlayerStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.Player;
import poker.repository.PlayerRepository;

@Service
@Log4j2
public class PlayerService {
    private final PlayerRepository playerRepo;

    public PlayerService(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public void updatePlayerStatus(Long playerId, PlayerStatus playerStatus) {
        playerRepo.updatePlayerStatus(playerId, playerStatus);
    }

    public Player getPlayerByUserId(long userId) {
        return playerRepo.findPlayerByUserId(userId);
    }

    public void updateProfileInfo(long playerId, String nickname) {
        playerRepo.updatePlayerNickname(playerId, nickname);
        log.info("Updated player nickname to {}", nickname);
    }
}
