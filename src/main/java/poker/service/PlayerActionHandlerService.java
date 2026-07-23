package poker.service;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.game.GameState;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.engine.GameEngineRegistry;
import poker.service.handler.DBPlayerActionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("PlayerActionHandlerService")
@Log4j2
@ToString
public class PlayerActionHandlerService {
    private final GameEngineRegistry gameEngineRegistry;
    private final Map<PlayerAction, DBPlayerActionHandler> dbPlayerActionHandlerMap;

    public PlayerActionHandlerService(GameEngineRegistry gameEngineRegistry,
                                      List<DBPlayerActionHandler> dbPlayerActionHandlerList) {
        this.gameEngineRegistry = gameEngineRegistry;

        dbPlayerActionHandlerMap = new HashMap<>();
        for (DBPlayerActionHandler dbPlayerActionHandler : dbPlayerActionHandlerList) {
            dbPlayerActionHandlerMap.put(dbPlayerActionHandler.supportsPlayerAction(), dbPlayerActionHandler);
        }
    }

    public void handle(PlayerActionData pad) {
        log.info("Handle {} player id {} game id {}",
                pad.getPlayerAction().getActionName(), pad.getPlayerDetails().getPlayer().getId(), pad.getGameId());

        var gameEngine = gameEngineRegistry.getGameEngine(pad.getGameId());

        GameState snapshot = gameEngine.snapshot();
        log.debug("Snapshot: {}", snapshot);

        gameEngine.handlePlayerAction(pad);
        log.debug("Game state after handling action: {}", gameEngine.getGameState());

        var dbPlayerActionHandler = dbPlayerActionHandlerMap.get(pad.getPlayerAction());

        boolean isSuccess = dbPlayerActionHandler.handleAction(gameEngine, pad);
        if (!isSuccess) {
            gameEngine.rollback(snapshot);
            log.error("Rollback game state to {}", snapshot);
        }
    }
}
