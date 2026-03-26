package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.dto.player.PlayerDTO;
import poker.game.*;
import poker.model.GameTable;
import poker.model.Player;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class GameManagerService {
    private final GameRegistry gameRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;

    public GameManagerService(GameRegistry gameRegistry, GameService gameService, PlayerService playerService, GameTableService gameTableService) {
        this.gameRegistry = gameRegistry;
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameTableService = gameTableService;
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

    public GameStateDTO startGame(Long gameId) {
        var gameTables = gameTableService.getGameTablesByGameId(gameId);
        var playerIdsList = gameTables.stream()
            .map(GameTable::getPlayerId)
            .toList();

        var game = gameService.getGameById(gameId);
        playerService.preparePlayersForGame(playerIdsList, game.getBuyIn());
        gameService.startGame(game);

        List<Player> players = playerService.getPlayersByIds(playerIdsList);
        List<PlayerDTO> playerDTOList = PlayerConverter.toListDTO(players);

        GameDTO gameDTO = GameConverter.toDTO(game, gameTables.size());
        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
