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

    public boolean isPlayerExistsByNickname(String nickname) {
        return playerRepo.existsByNickname(nickname);
    }

    public Player createPlayer(String nickname) {
        var player = Player.builder()
            .nickname(nickname)
            .status(PlayerStatus.NOT_IN_GAME)
            .chips(0)
            .currentBet(0)
            .build();

        var newPlayer = playerRepo.save(player);
        log.info("Player created {}", newPlayer);
        return newPlayer;
    }

    public Player getPlayerByUserId(long userId) {
        return playerRepo.findPlayerByUserId(userId);
    }

    public void updateProfileInfo(long playerId, String nickname) {
        playerRepo.updatePlayerNickname(playerId, nickname);
        log.info("Updated player id {} nickname to {}", playerId, nickname);
    }

    public void updatePlayerStatus(Long playerId, PlayerStatus playerStatus) {
        playerRepo.updatePlayerStatus(playerId, playerStatus);
        log.info("Player id {} status updated to {}", playerId, playerStatus);
    }
}
