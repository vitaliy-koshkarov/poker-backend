package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import poker.dto.game.GameStateConverter;
import poker.dto.game.GameStateDTO;
import poker.game.GameEngineRegistry;
import poker.game.GameState;

@Service("ResponseGenerator")
@RequiredArgsConstructor
@ToString
public class GameStateResponseGenerator {
    private final GameEngineRegistry gameEngineRegistry;

    public GameStateDTO generateResponse(long gameId) {
        GameState gameState = gameEngineRegistry.getGameEngine(gameId)
            .getCurrentGameState();
        return GameStateConverter.toDTO(gameState);
    }
}
