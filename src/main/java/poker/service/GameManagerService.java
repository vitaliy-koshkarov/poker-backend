package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.game.*;
import poker.model.GameTable;
import poker.model.event.GameEvent;

import java.sql.Timestamp;
import java.util.ArrayList;

@Service
@Log4j2
public class GameManagerService {
    private final GameRegistry gameRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;
    private final GameEventService gameEventService;

    public GameManagerService(GameRegistry gameRegistry, GameService gameService,
                              PlayerService playerService, GameTableService gameTableService,
                              GameEventService gameEventService) {
        this.gameRegistry = gameRegistry;
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameTableService = gameTableService;
        this.gameEventService = gameEventService;
    }

    public GameStateDTO handleAction(Long gameId, Long playerId, PlayerAction action) {
        log.info("Handling action {} from player id {}", action, playerId);

        var gameEngine = gameRegistry.getGameEngine(gameId);

        var game = gameService.getGameById(gameId);
        var gameTableList = gameTableService.getGameTablesByGameId(gameId);

        var playerIdsList = new ArrayList<Long>();
        for (GameTable gameTable : gameTableList) {
            playerIdsList.add(gameTable.getPlayerId());
        }
        var players = playerService.getPlayersByIds(playerIdsList);

//        update game state in-memory
        gameEngine.handleAction(action, playerId, game);

//        update game state in DB
        if (PlayerAction.JOIN_GAME.equals(action)) {
//            todo: update game state here. Player's state is updated in controller during Subscribe
            log.info("Player id {} joined to game id {}", playerId, gameId);
        } else if (PlayerAction.START_GAME.equals(action)) {
//            todo: update game state here
//            playerService.updatePlayers(players);
//            gameService.updateGame(game);
            log.info("Player id {} started game id {}", playerId, gameId);
        } else if (PlayerAction.DISCONNECT.equals(action)) {
//            player's status updates in WebSocketEventListener
            log.info("Player id {} disconnected from game id {}", playerId, gameId);
        } else if (PlayerAction.FOLD.equals(action)) {
        } else if (PlayerAction.BET.equals(action)) {
        } else if (PlayerAction.CHECK.equals(action)) {
        } else if (PlayerAction.ALL_IN.equals(action)) {
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
        log.info("Handled action {} from player id {}, event id {}", playerId, action, eventId);

        var gameDTO = GameConverter.toDTO(game, gameTableList.size());
        var playerDTOList = PlayerConverter.toListDTO(players);

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
