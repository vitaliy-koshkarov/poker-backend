package poker.core;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.game.texasholdem.THPot;
import poker.core.game.texasholdem.THTable;
import poker.model.Game;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("GameRegistry")
@Log4j2
@ToString
public class GameRegistry {
    private final Map<Long, GameTable> gameTablesMap = new ConcurrentHashMap<>();

    public void registerNewGame(Game game, long roundId) {
        long gameId = game.getId();
        log.info("Registering game id {} with blinds {}/{}", gameId, game.getSmallBlind(), game.getBigBlind());

        GameTable gameTable = new THTable(gameId, game.getName(), game.getCreatorPlayerId(),
            game.getMaxPlayers(), game.getBuyIn(), GameStatus.getGameStatusByInt(game.getStatus()),
            game.getSmallBlind(), game.getBigBlind(), new THPot(game.getPotId()), roundId);

        gameTablesMap.put(gameId, gameTable);
        log.info("Game id {} registered", gameId);
    }

    public void recoverGame(Game game) {
    }

    public GameTable getGameTable(long gameId) {
        return gameTablesMap.get(gameId);
    }

    public Collection<GameTable> getGameTableCollection() {
        return gameTablesMap.values();
    }

    public void removeGame(long gameId) {
        gameTablesMap.remove(gameId);
        log.info("Game id {} removed from engine", gameId);
    }
}
