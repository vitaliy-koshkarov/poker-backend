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

    public Player getPlayerById(Long id) {
//        TODO: return stub instance
        return playerRepo.findById(id)
            .orElseGet(() -> {
                log.error("Error get player by id {}", id);
                return Player.builder()
                    .id(-1L)
                    .nickname("empty name")
                    .status(PlayerStatus.NOT_IN_GAME)
                    .chips(-1)
                    .currentBet(-1)
                    .build();
            });
    }

    public Player updatePlayer(Player player) {
        return playerRepo.save(player);
    }
}
