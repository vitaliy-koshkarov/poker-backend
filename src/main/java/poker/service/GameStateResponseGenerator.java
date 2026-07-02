package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
import poker.dto.game.GameStateConverter;
import poker.dto.game.GameStateDTO;
import poker.core.engine.GameEngineRegistry;
import poker.core.game.GameState;

import java.util.LinkedList;
import java.util.List;

@Component("GameStateResponseGenerator")
@RequiredArgsConstructor
@Log4j2
@ToString
public class GameStateResponseGenerator {
    private final GameEngineRegistry gameEngineRegistry;

    public List<GameStateDTO> getGamesListForLobby() {
        var gameStateDTOInLobbyList = new LinkedList<GameStateDTO>();

        for (GameEngine gameEngine : gameEngineRegistry.getGameEngineCollection()) {
            gameStateDTOInLobbyList.add(GameStateConverter.toGameStateDTOInLobby(gameEngine.getGameState()));
        }

        return gameStateDTOInLobbyList;
    }

    public GameStateDTO generateResponse(long gameId) {
        GameState gameState = gameEngineRegistry.getGameEngine(gameId).getGameState();
        log.debug("Game state {}", gameState);
        return GameStateConverter.toGameFlowGameStateDTO(gameState);
    }
}
