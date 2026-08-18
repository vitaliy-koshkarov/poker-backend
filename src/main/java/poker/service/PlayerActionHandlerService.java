package poker.service;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.GameEngine;
import poker.core.game.GameState;
import poker.core.game.texasholdem.THEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.GameRegistry;
import poker.service.handler.DBPlayerActionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("PlayerActionHandlerService")
@Log4j2
@ToString
public class PlayerActionHandlerService {
    private final GameRegistry gameRegistry;
    private final Map<PlayerAction, DBPlayerActionHandler> dbPlayerActionHandlerMap;

    public PlayerActionHandlerService(GameRegistry gameRegistry,
                                      List<DBPlayerActionHandler> dbPlayerActionHandlerList) {
        this.gameRegistry = gameRegistry;

        dbPlayerActionHandlerMap = new HashMap<>();
        for (DBPlayerActionHandler dbPlayerActionHandler : dbPlayerActionHandlerList) {
            dbPlayerActionHandlerMap.put(dbPlayerActionHandler.supportsPlayerAction(), dbPlayerActionHandler);
        }
    }

    public void handle(PlayerActionData pad) {
        log.info("Handle {} player id {} game id {}",
            pad.getPlayerAction().getActionName(), pad.getPlayerId(), pad.getGameId());

        var gameTable = gameRegistry.getGameTable(pad.getGameId());
        GameEngine engine = new THEngine(gameTable);

        GameState snapshot = engine.snapshot();
        log.debug("Snapshot: {}", snapshot);

        try {
            engine.handlePlayerAction(pad);
        } catch (Exception ex) {
            log.error("{}: {}. Place: {}, game id {}",
                ex.getCause(), ex.getMessage(), ex.getStackTrace()[0], pad.getGameId());

            engine.rollback(snapshot);

            throw new RuntimeException("Engine exception, game id " + pad.getGameId());
        }
        log.debug("Game state after handling action: {}", engine.getGameState());

        var dbPlayerActionHandler = dbPlayerActionHandlerMap.get(pad.getPlayerAction());

        boolean isSuccess = dbPlayerActionHandler.handleAction(gameTable, pad);
        if (!isSuccess) {
            engine.rollback(snapshot);
            log.error("Rollback game state to {}", snapshot);
        }
    }
}
