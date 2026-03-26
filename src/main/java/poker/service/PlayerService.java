package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.Player;
import poker.model.PlayerStatus;
import poker.repository.PlayerRepository;

import java.sql.Timestamp;
import java.util.List;

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

    public Player createPlayer(String nickname, Timestamp now) {
        var player = Player.builder()
            .nickname(nickname)
            .status(PlayerStatus.NOT_IN_GAME.getStatus())
            .chips(0)
            .currentBet(0)
            .createdAt(now)
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
        log.info("Updated nickname to {}, player id {}", nickname, playerId);
    }

    public void updatePlayerStatus(Long playerId, Integer playerStatus) {
        playerRepo.updatePlayerStatus(playerId, playerStatus);
        log.info("Player id {} status updated to {}", playerId, playerStatus);
    }

    public List<Player> getPlayersByIds(List<Long> playerIdsList) {
        return playerRepo.findAllById(playerIdsList);
    }

    public void updatePlayers(List<Player> players) {
        playerRepo.saveAll(players);
        log.info("Updated players {}", players);
    }
}
