package poker.core.engine;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GamePot;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.game.texasholdem.THEngine;
import poker.core.game.texasholdem.THPot;
import poker.core.game.texasholdem.THTable;
import poker.model.Game;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("GameEngineRegistry")
@Log4j2
@ToString
public class GameEngineRegistry {
    private final Map<Long, GameEngine> gameEngineMap = new ConcurrentHashMap<>();

    public void registerGame(Game game) {
        long gameId = game.getId();
        log.info("Registering game id {} with blinds {}/{}", gameId, game.getSmallBlind(), game.getBigBlind());

        GamePot pot = new THPot(game.getPotId());
//        TODO: restore pot, deck, community cards, players list, player's cards
//        TODO: refactoring creation of the THTable
//        TODO: When game load, all fields must be correctly set
        GameTable table = new THTable(gameId, game.getName(), game.getCreatorPlayerId(),
            game.getMaxPlayers(), game.getBuyIn(), GameStatus.getGameStatusByInt(game.getStatus()),
            game.getSmallBlind(), game.getBigBlind(), pot);
        GameEngine engine = new THEngine(table);

        gameEngineMap.put(gameId, engine);
        log.info("Game id {} registered", gameId);
    }

    public GameEngine getGameEngine(long gameId) {
        return gameEngineMap.get(gameId);
    }

    public Collection<GameEngine> getGameEngineCollection() {
        return gameEngineMap.values();
    }

    public void removeGame(long gameId) {
        gameEngineMap.remove(gameId);
        log.info("Game id {} removed from engine", gameId);
    }
}
