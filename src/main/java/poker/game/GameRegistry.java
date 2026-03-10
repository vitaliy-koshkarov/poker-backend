package poker.game;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class GameRegistry {
    private final Map<Long, GameEngine> gameEngineMap = new ConcurrentHashMap<>();

    public GameEngine registerGame(Long gameId) {
        GameEngine engine = new GameEngineStub();
        gameEngineMap.put(gameId, engine);
        return engine;
    }

    public GameEngine getGameEngine(Long gameId) {
        return gameEngineMap.get(gameId);
    }
}
