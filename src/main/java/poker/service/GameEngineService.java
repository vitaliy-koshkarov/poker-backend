package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.game.*;
import poker.game.playeraction.PlayerAction;
import poker.model.GameTable;
import poker.model.event.GameEvent;
import poker.service.handler.PlayerActionHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

@Service("GameEngineService")
@Log4j2
public class GameEngineService {
    private final Map<String, PlayerActionHandler> playerActionHandlerMap;
    private final GameRegistry gameRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;
    private final GameEventService gameEventService;

    @Autowired
    public GameEngineService(Map<String, PlayerActionHandler> playerActionHandlerMap, GameRegistry gameRegistry,
                             GameService gameService, PlayerService playerService,
                             GameTableService gameTableService, GameEventService gameEventService) {
        this.playerActionHandlerMap = playerActionHandlerMap;
        this.gameRegistry = gameRegistry;
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameTableService = gameTableService;
        this.gameEventService = gameEventService;
    }

    public GameStateDTO handlePlayerAction(Long gameId, Long playerId, PlayerAction action) {
        log.info("Handling action {} from player id {}", action, playerId);

        var gameEngine = gameRegistry.getGameEngine(gameId);

        var game = gameService.getGameById(gameId);
        var gameTableList = gameTableService.getGameTablesByGameId(gameId);

        var playerIdsList = new ArrayList<Long>();
        for (GameTable gameTable : gameTableList) {
            playerIdsList.add(gameTable.getPlayerId());
        }
        var players = playerService.getPlayersByIds(playerIdsList);
        var actionInitiatorPlayer = playerService.getPlayerById(playerId);

        var playerActionHandler = playerActionHandlerMap.get(action.getActionName());
        if (playerActionHandler != null) {
            playerActionHandler.handleAction(gameEngine, game, actionInitiatorPlayer);
        } else {
            log.info("Suspicious action {} from player id {} in game id {}", action, playerId, game.getId());
        }

        var gameEventData = gameEngine.getGameEventData();

        var gameEvent = GameEvent.builder()
            .gameId(gameId)
            .playerId(playerId)
            .type(action.getType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        Long eventId = gameEventService.saveEvent(gameEvent);
        log.info("Handled action {} from player id {}, event id {}", action, playerId, eventId);

        var gameDTO = GameConverter.toDTO(game, gameTableList.size());
        var playerDTOList = PlayerConverter.toListDTO(players);

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
