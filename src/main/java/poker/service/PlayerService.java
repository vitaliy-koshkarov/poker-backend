package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.dto.profile.ProfileInfoRequest;
import poker.model.Player;
import poker.core.player.PlayerStatus;
import poker.repository.PlayerRepository;
import poker.util.Util;

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
    public void updateProfileInfo(ProfileInfoRequest request) {
        long playerId = Util.getPlayerDetailsFronCtx().getPlayer().getId();
        String nickname = request.nickname();

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

    public void updatePlayerStatusAndCurrentBet(long playerId, PlayerStatus status, int bet) {
        playerRepo.updateStatusAndCurrentBet(playerId, status.getIntStatus(), bet);
        log.debug("Player id {} current bet {}", playerId, bet);
    }

    public void updateStatusAndChipsAndCurrentBet(long playerId, PlayerStatus status, int chips, int currentBet) {
        playerRepo.updateStatusAndChipsAndCurrentBet(playerId, status.getIntStatus(), chips, currentBet);
    }
}
