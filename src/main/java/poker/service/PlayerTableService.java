package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.PlayerTable;
import poker.repository.PlayerTableRepository;

import java.util.Collections;
import java.util.Set;

@Service
@Log4j2
public class PlayerTableService {
    private final PlayerTableRepository playerTableRepo;

    public PlayerTableService(PlayerTableRepository playerTableRepository) {
        this.playerTableRepo = playerTableRepository;
    }

    public PlayerTable createPlayerTable(Long userId, Long playerId) {
        var playerTable = PlayerTable.builder()
            .userId(userId)
            .playerId(playerId)
            .tableIds(Collections.emptySet())
            .build();

        var newPlayerTable = playerTableRepo.save(playerTable);
        log.info("Player table created {}", newPlayerTable);
        return newPlayerTable;
    }

    public PlayerTable getPlayerTableByUserAndPlayerIds(Long userId, Long playerId) {
        return playerTableRepo.findPlayerTableByUserAndPlayerIds(userId, playerId);
    }

    public void updatePlayerTable(Long playerTableId, Set<Long> tableIds, Long userId) {
        playerTableRepo.updatePlayerTable(playerTableId, tableIds);
    }
}
