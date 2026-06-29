package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.game.GameState;
import poker.core.player.PlayerActionData;
import poker.core.engine.GameEngineRegistry;
import poker.service.handler.DBPlayerActionHandler;

import java.util.Map;

@Service("PlayerActionHandlerService")
@Log4j2
@RequiredArgsConstructor
@ToString
public class PlayerActionHandlerService {
    private final GameEngineRegistry gameEngineRegistry;
    private final Map<String, DBPlayerActionHandler> dbPlayerActionHandlerMap;
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameSeatService gameSeatService;

    public void handlePlayerAction(PlayerActionData pad) {
        String actionName = pad.getPlayerAction().getActionName();
        log.info("Handling action {} from player id {} in game {}",
            actionName, pad.getPlayerDetails().getPlayer().getId(), pad.getGameId());
//        TODO: Implement:
//              1. Snapshot of game state
//              2. Handle action in Engine
//              3. DB + Event in a single transaction
//              3.1. If success -> do nothing on this step
//              3.2. If fails -> rollback engine to snapshot
//              ✓ 4. Return response from engine (already implemented in GameStateReportGenerator)

        var gameEngine = gameEngineRegistry.getGameEngine(pad.getGameId());
        GameState snapshot = gameEngine.getCurrentGameState();

        gameEngine.handlePlayerAction(pad);

        var dbPlayerActionHandler = dbPlayerActionHandlerMap.get(actionName);
        // todo: TX: DB handling + game action event generation
        boolean isSuccess = dbPlayerActionHandler.handleAction(gameEngine, pad);
        if (!isSuccess) {
            gameEngine.rollback(snapshot);
        }
    }
}
