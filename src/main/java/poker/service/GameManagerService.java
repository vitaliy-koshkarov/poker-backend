package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.game.*;
import poker.model.GameTable;
import poker.model.event.EventData;
import poker.model.event.GameEvent;
import poker.util.Util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class GameManagerService {
    private final GameRegistry gameRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;
    private final GameEventService gameEventService;
    private final PotService potService;

    public GameManagerService(GameRegistry gameRegistry, GameService gameService,
                              PlayerService playerService, GameTableService gameTableService,
                              GameEventService gameEventService, PotService potService) {
        this.gameRegistry = gameRegistry;
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameTableService = gameTableService;
        this.gameEventService = gameEventService;
        this.potService = potService;
    }

    public void registerGame(Long gameId) {
        GameEngine gameEngine = gameRegistry.registerGame(gameId);
        log.info("Registered gameDTO id {}, gameDTO state {}", gameId, gameEngine.getGameState());
    }

    public GameStateDTO handleAction(Long gameId, Long playerId, PlayerAction action) {
        log.info("Register action {} from player id {}", action, playerId);

        var game = gameService.getGameById(gameId);

        long eventId = Util.DEFAULT_LONG_VALUE;

        if (PlayerAction.JOIN_GAME.equals(action)) {
            var joinPlayerEvent = GameEvent.builder()
                .gameId(gameId)
                .playerId(playerId)
                .type(action.getType())
                .data(EventData.builder()
                    .value(PlayerAction.JOIN_GAME.getDescription())
                    .build()
                )
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

            eventId = gameEventService.saveEvent(joinPlayerEvent);

            var gameTables = gameTableService.getGameTablesByGameId(game.getId());
            var playerIdsList = new ArrayList<Long>();
            for (GameTable gameTable : gameTables) {
                playerIdsList.add(gameTable.getPlayerId());
            }

            var players = playerService.getPlayersByIds(playerIdsList);
            var playerDTOList = PlayerConverter.toListDTO(players);

            var gameDTO = GameConverter.toDTO(game, gameTables.size());

            return GameStateDTO.builder()
                .gameDTO(gameDTO)
                .playerDTOList(playerDTOList)
                .build();

        } else if (PlayerAction.DISCONNECT.equals(action)) {
            var disconnectPlayerEvent = GameEvent.builder()
                .gameId(gameId)
                .playerId(playerId)
                .type(action.getType())
                .data(EventData.builder()
                    .value(PlayerAction.DISCONNECT.getDescription())
                    .build()
                )
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

            eventId = gameEventService.saveEvent(disconnectPlayerEvent);

            var gameTables = gameTableService.getGameTablesByGameId(game.getId());
            var playerIdsList = new ArrayList<Long>();
            for (GameTable gameTable : gameTables) {
                playerIdsList.add(gameTable.getPlayerId());
            }

            var players = playerService.getPlayersByIds(playerIdsList);
            var playerDTOList = PlayerConverter.toListDTO(players);

            var gameDTO = GameConverter.toDTO(game, gameTables.size());

            return GameStateDTO.builder()
                .gameDTO(gameDTO)
                .playerDTOList(playerDTOList)
                .build();

        } else if (PlayerAction.START_GAME.equals(action)) {
//            update state in-memory
            var pot = potService.getPotById(game.getPotId());
            var gameTableList = gameTableService.getGameTablesByGameId(gameId);

            var playerIdList = new ArrayList<Long>();
            for (GameTable gameTable : gameTableList) {
                playerIdList.add(gameTable.getPlayerId());
            }

            var players = playerService.getPlayersByIds(playerIdList);

            Long dealerId = game.getDealerId();
            Long activePlayerId = game.getActivePlayerId();

            List<Object> communityCards = Collections.emptyList(); // TODO: community cards are in engine and GameEvent.data in DB

            var gameEngine = gameRegistry.getGameEngine(gameId);
            var startGameEvent = gameEngine.handleAction(game, playerId, dealerId, activePlayerId, players, communityCards, action, pot);

            eventId = gameEventService.saveEvent(startGameEvent);

//            update state in DB
            playerService.updatePlayers(players);
            gameService.updateGame(game);

            var playerDTOList = PlayerConverter.toListDTO(players);
            var gameDTO = GameConverter.toDTO(game, gameTableList.size());

            return GameStateDTO.builder()
                .gameDTO(gameDTO)
                .playerDTOList(playerDTOList)
                .build();
        }

        log.info("Registered game event {} by player {}", eventId, playerId);


        var gameTableList = gameTableService.getGameTablesByGameId(gameId);

        var playerIdList = new ArrayList<Long>();
        for (GameTable gameTable : gameTableList) {
            playerIdList.add(gameTable.getPlayerId());
        }

        var players = playerService.getPlayersByIds(playerIdList);
        var playerDTOList = PlayerConverter.toListDTO(players);

        var gameDTO = GameConverter.toDTO(game, gameTableList.size());

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
