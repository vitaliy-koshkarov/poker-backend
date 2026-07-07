package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerActionData;
import poker.model.event.GameEvent;
import poker.model.event.GameEventFactory;
import poker.repository.GameEventRepository;

@Service
@Log4j2
@RequiredArgsConstructor
public class GameEventService {
    private final GameEventRepository gameEventRepo;

    public long createAndSaveEvent(GameEngine engine, PlayerActionData pad) {
        GameEvent gameEvent = GameEventFactory.create(engine, pad);
        var savedEvent = gameEventRepo.save(gameEvent);
        log.info("Saved game event {}", savedEvent);
        return savedEvent.getId();
    }
}
