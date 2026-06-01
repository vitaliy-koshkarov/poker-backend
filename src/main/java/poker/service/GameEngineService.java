package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.game.*;
import poker.game.texasholdem.THEngine;
import poker.model.GameStatus;
import poker.model.GameTable;
import poker.model.event.GameEvent;

import java.sql.Timestamp;
import java.util.ArrayList;

@Service
@Log4j2
public class GameEngineService {
    private final GameRegistry gameRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;
    private final GameEventService gameEventService;

    public GameEngineService(GameRegistry gameRegistry, GameService gameService,
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
        var actionInitiatorPlayer = playerService.getPlayerById(playerId);

//        update game state in-memory
        gameEngine.handleAction(action, game, actionInitiatorPlayer);

//        update game state in DB
        if (PlayerAction.JOIN_GAME.equals(action)) {
//            player updates in SUBSCRIBE Controller method
            log.info("Player id {} joined to game id {}", playerId, gameId);
        } else if (PlayerAction.START_GAME.equals(action)) {
            game.setStatus(GameStatus.PRE_FLOP.getStatus());
            game.setStartedAt(new Timestamp(System.currentTimeMillis()));

            long dealerId = ((THEngine) gameEngine).getDealerId();
            game.setDealerId(dealerId);

            long activePlayerId = ((THEngine) gameEngine).getActivePlayerId();
            game.setActivePlayerId(activePlayerId);

            gameService.updateGame(game);
//            TODO: update player's statuses
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
        log.info("Handled action {} from player id {}, event id {}", action, playerId, eventId);

        var gameDTO = GameConverter.toDTO(game, gameTableList.size());
        var playerDTOList = PlayerConverter.toListDTO(players);

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
