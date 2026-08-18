package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.game.GameTable;
import poker.core.player.PlayerActionData;
import poker.model.event.GameEvent;
import poker.repository.GameEventRepository;
import poker.service.event.GameEventFactoryProvider;

@Service
@Log4j2
@RequiredArgsConstructor
public class GameEventService {
    private final GameEventFactoryProvider gameEventFactoryProvider;
    private final GameEventRepository gameEventRepo;

    public long createAndSaveEvent(GameTable gameTable, PlayerActionData pad) {
        GameEvent gameEvent = gameEventFactoryProvider.create(gameTable, pad);
        var savedEvent = gameEventRepo.save(gameEvent);
        log.debug("Saved game event {}", savedEvent);
        return savedEvent.getId();
    }
}
