package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngineRegistry;
import poker.dto.game.GameConverter;
import poker.dto.game.GameStateDTO;
import poker.dto.player.PlayerConverter;
import poker.core.player.PlayerAction;
import poker.model.Game;
import poker.model.GameSeat;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.model.event.GameEvent;
import poker.model.event.GameEventData;
import poker.service.handler.PlayerActionHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("GameEngineService")
@Log4j2
@RequiredArgsConstructor
@Deprecated(since = "Will be removed soon. Use PlayerActionHandlerService instead")
public class GameEngineService {
    private final Map<String, PlayerActionHandler> playerActionHandlerMap;
    private final GameEngineRegistry gameEngineRegistry;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameSeatService gameSeatService;
    private final GameEventService gameEventService;

    @Transactional(rollbackFor = Exception.class)
    public GameStateDTO handlePlayerAction(long gameId, PlayerDetails playerDetails, PlayerAction action) {
        long playerId = playerDetails.getPlayer().getId();
        log.info("Handling action {} from player id {}", action, playerId);

        var gameEngine = gameEngineRegistry.getGameEngine(gameId);
        var game = gameService.getGameById(gameId);

        var playerActionHandler = playerActionHandlerMap.get(action.getActionName());
        if (playerActionHandler != null) {
            playerActionHandler.handleAction(gameEngine, game, playerDetails);
        } else {
            log.info("Suspicious action {} from player id {} in game id {}", action, playerId, game.getId());
        }

        createAndSaveGameEvent(gameId, playerId, action);

        return returnGameStateDTO(game);
    }

    private void createAndSaveGameEvent(long gameId, long playerId, PlayerAction action) {

        var gameEvent = GameEvent.builder()
            .gameId(gameId)
            .playerId(playerId)
            .type(action.getType())
            .gameEventData(GameEventData.builder()
                .communityCards(Collections.emptyList())
                .communityCards(Collections.emptyList())
                .build()
            )
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        long eventId = gameEventService.saveEvent(gameEvent);
        log.info("Handled action {} from player id {}, event id {}", action, playerId, eventId);
    }

    private GameStateDTO returnGameStateDTO(Game game) {
        List<GameSeat> gameSeatList = gameSeatService.getGameSeatsByGameId(game.getId());
        var playerIdsList = new ArrayList<Long>();
        for (GameSeat gameSeat : gameSeatList) {
            playerIdsList.add(gameSeat.getPlayerId());
        }
        List<Player> players = playerService.getPlayersByIds(playerIdsList);

        var gameDTO = GameConverter.toDTO(game, gameSeatList.size());
        var playerDTOList = PlayerConverter.toListDTO(players);

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
