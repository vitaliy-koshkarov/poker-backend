package poker.game;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.texasholdem.THEngine;
import poker.game.texasholdem.THTable;
import poker.model.GameStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class GameRegistry {
    private final Map<Long, GameEngine> gameEngineMap = new ConcurrentHashMap<>();

    public void registerGame(Long gameId, int buyIn) {
//        TODO: externalize to service's parameters
        int smallBlind = buyIn / 100;
        int bigBlind = buyIn / 50;

        var thTable = new THTable(gameId, GameStatus.WAITING_FOR_PLAYERS, smallBlind, bigBlind);
        GameEngine engine = new THEngine(thTable);

        gameEngineMap.put(gameId, engine);
        log.info("Game engine added to game engine registry, game id {}", gameId);
    }

    public GameEngine getGameEngine(Long gameId) {
        return gameEngineMap.get(gameId);
    }
}
