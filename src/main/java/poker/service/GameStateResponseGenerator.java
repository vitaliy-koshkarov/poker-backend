package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameStateConverter;
import poker.dto.game.GameStateDTO;
import poker.core.engine.GameEngineRegistry;
import poker.core.game.GameState;

@Service("GameStateResponseGenerator")
@RequiredArgsConstructor
@Log4j2
@ToString
public class GameStateResponseGenerator {
    private final GameEngineRegistry gameEngineRegistry;

    public GameStateDTO generateResponse(long gameId) {
        GameState gameState = gameEngineRegistry.getGameEngine(gameId)
            .getCurrentGameState();
        log.debug("Game state {}", gameState);
        return GameStateConverter.toDTO(gameState);
    }
}
