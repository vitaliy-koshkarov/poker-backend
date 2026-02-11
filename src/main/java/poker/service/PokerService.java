package poker.service;

import lombok.extern.log4j.Log4j2;
import poker.dto.game.GameTableDTO;
import org.springframework.stereotype.Service;
import poker.repository.GameTableRepository;

@Service
@Log4j2
public class PokerService {
    private final GameTableRepository gameTableRepo;

    public PokerService(GameTableRepository gameTableRepository) {
        this.gameTableRepo = gameTableRepository;
    }

    public GameTableDTO handleAction(String sessionId, Long gameTableId) throws Exception {
        log.info("PokerService handle game table id {} for session {}", gameTableId, sessionId);

        var gameTable = gameTableRepo.findById(gameTableId).orElseThrow(() -> {
            log.error("Failed to get game table with id {} for session {}", gameTableId, sessionId);
            return new Exception("Failed to get game table with id " + gameTableId + " for session " + sessionId);
        });

        var gameTableDTO = GameTableDTO.builder()
            .id(gameTable.getId())
            .currentPlayers(gameTable.getCurrentPlayers().size())
            .maxPlayers(gameTable.getMaxPlayers())
            .buyIn(gameTable.getBuyIn())
            .name(gameTable.getName())
            .build();

        log.info("PokerService handled game table {} for session {}. {}", gameTableId, sessionId, gameTableDTO);

        return gameTableDTO;
    }
}
