package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GameTable;
import poker.core.game.texasholdem.THEngine;
import poker.dto.game.GameDTO;
import poker.dto.game.GameStateConverter;
import poker.core.GameRegistry;
import poker.core.game.GameState;

import java.util.LinkedList;
import java.util.List;

@Component("GameStateResponseGenerator")
@RequiredArgsConstructor
@Log4j2
@ToString
public class GameStateResponseGenerator {
    private final GameRegistry gameRegistry;

    public List<GameDTO> getGamesListForLobby() {
        var gameStateDTOInLobbyList = new LinkedList<GameDTO>();

        for (GameTable gameTable : gameRegistry.getGameTableCollection()) {
            gameStateDTOInLobbyList.add(GameStateConverter.forLobbyGameStateDTO(new THEngine(gameTable).getGameState()));
        }

        return gameStateDTOInLobbyList;
    }

    public GameDTO generateResponse(long gameId) {
        GameTable gameTable = gameRegistry.getGameTable(gameId);
        GameState gameState = new THEngine(gameTable).getGameState();
        log.debug("Game state {}", gameState);
        return GameStateConverter.toGameFlowGameStateDTO(gameState);
    }
}
