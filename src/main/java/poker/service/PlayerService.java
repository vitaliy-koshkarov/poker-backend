package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.core.player.PlayerStatus;
import poker.repository.PlayerRepository;

import java.sql.Timestamp;

@Service("PlayerService")
@Log4j2
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepo;

    @Transactional(readOnly = true)
    public boolean isPlayerExistsByNickname(String nickname) {
        return playerRepo.existsByNickname(nickname);
    }

    public Player createPlayer(String nickname, Timestamp now) {
        var player = Player.builder()
            .nickname(nickname)
            .status(PlayerStatus.NOT_IN_GAME.getIntStatus())
            .chips(0)
            .currentBet(0)
            .createdAt(now)
            .build();

        var newPlayer = playerRepo.save(player);
        log.info("Player created {}", newPlayer);
        return newPlayer;
    }

    @Transactional(readOnly = true)
    public Player getPlayerByUserId(long userId) {
        return playerRepo.findPlayerByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProfileInfo(PlayerDetails playerDetails, String nickname) {
        log.info("Update nickname request to {}", nickname);
        long playerId = playerDetails.getPlayer().getId();
        playerRepo.updatePlayerNickname(playerId, nickname);
        log.info("Updated nickname to {}, player id {}", nickname, playerId);
    }

    public void updatePlayerStatusAndChips(long playerId, int chips, PlayerStatus playerStatus) {
        playerRepo.updatePlayerStatusAndChips(playerId, chips, playerStatus.getIntStatus());
        log.debug("Player id {} chips {} status {}", playerId, chips, playerStatus);
    }

    public void updatePlayerStatus(long playerId, PlayerStatus playerStatus) {
        playerRepo.updateStatus(playerId, playerStatus.getIntStatus());
        log.debug("Player id {} status {}", playerId, playerStatus);
    }
}
