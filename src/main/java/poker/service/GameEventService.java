package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.event.GameEvent;
import poker.repository.GameEventRepository;

@Service
@Log4j2
public class GameEventService {
    private final GameEventRepository gameEventRepo;

    public GameEventService(GameEventRepository gameEventRepo) {
        this.gameEventRepo = gameEventRepo;
    }

    public Long saveEvent(GameEvent gameEvent) {
        var savedGameEvent = gameEventRepo.save(gameEvent);
        log.info("Saved game event {}", gameEvent);
        return savedGameEvent.getId();
    }
}
