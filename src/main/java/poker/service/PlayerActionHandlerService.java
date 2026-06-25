package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.game.GameEngineRegistry;
import poker.game.PlayerAction;
import poker.model.GameTable;
import poker.model.PlayerDetails;

import java.util.LinkedList;
import java.util.List;

@Service("PlayerActionHandlerService")
@Log4j2
@RequiredArgsConstructor
@ToString
public class PlayerActionHandlerService {
    private final GameEngineRegistry gameEngineRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;

    public void handlePlayerAction(long gameId, PlayerDetails playerDetails, PlayerAction playerAction) {
        log.info("Handling action {} from player id {} in game {}",
            playerAction.getActionName(), playerDetails.getPlayer().getId(), gameId);
//        TODO: Implement:
//              1. Snapshot of game state
//              2. Handle action in Engine
//              3. DB + Event in a single transaction
//              3.1. If success -> do next step
//              3.2. If fails -> rollback engine to snapshot
//              4. Return response from engine

//        GameEngine gameEngine = gameEngineRegistry.getGameEngine(gameId);
//        GameState snapshot = gameEngine.getCurrentGameState();
        // todo: TX: DB handling + game action event generation
    }

    @Deprecated(since = "Will be remove after implementing start game handling")
    public GameStateDTO getCurrentState(long gameId) {
        var game = gameService.getGameById(gameId);

        var gameTableList = gameTableService.getGameTablesByGameId(gameId);
        List<Long> playersIdsList = new LinkedList<>();
        for (GameTable gameTable : gameTableList) {
            playersIdsList.add(gameTable.getPlayerId());
        }

        var playersList = playerService.getPlayersByIds(playersIdsList);

        var gameDTO = GameConverter.toDTO(game, gameTableList.size());
        var playerDTOList = PlayerConverter.toListDTO(playersList);

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
