package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.game.*;

import java.util.UUID;

@Service
@Log4j2
public class GameManagerService {
    private final GameRegistry gameRegistry;

    public GameManagerService(GameRegistry gameRegistry) {
        this.gameRegistry = gameRegistry;
    }

    public void registerGame(Long gameId) {
        GameEngine gameEngine = gameRegistry.registerGame(gameId);
        log.info("Registered gameDTO id {}, gameDTO state {}", gameId, gameEngine.getGameState());
    }

    public GameState handleAction(Long gameId, Long playerId, PlayerAction action) {
        log.info("Handling action {} from player id {}", action, playerId);
        GameEngine engine = gameRegistry.getGameEngine(gameId);
        GameEvent gameEvent = engine.handleAction(playerId, action);
        UUID gameEventUuid = gameEvent.getEventUuid();
        log.info("Game event UUID {} player id {}", gameEventUuid, playerId);
//        TODO: save gameDTO event in DB
        GameState gameState = engine.getGameState();
        log.info("Game state after handling action {} from player id {}", gameState, playerId);
        return gameState;
    }
}
