package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.event.GameEvent;
import poker.repository.GameEventRepository;

@Service
@Log4j2
@RequiredArgsConstructor
public class GameEventService {
    private final GameEventRepository gameEventRepo;

    public long saveEvent(GameEvent gameEvent) {
        var savedGameEvent = gameEventRepo.save(gameEvent);
        log.info("Saved game event {}", gameEvent);
        return savedGameEvent.getId();
    }
}
