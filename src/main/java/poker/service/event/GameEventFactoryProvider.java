package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.event.GameEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class GameEventFactoryProvider {
    private final Map<PlayerAction, GameEventFactory> factories;

    public GameEventFactoryProvider(List<GameEventFactory> factoryList) {
        factories = new HashMap<>();
        for (GameEventFactory gameEventFactory : factoryList) {
            factories.put(gameEventFactory.supportsPlayerAction(), gameEventFactory);
        }
    }

    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        return factories.get(pad.getPlayerAction()).create(engine, pad);
    }
}
