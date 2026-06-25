package poker.game;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.texasholdem.THEngine;
import poker.game.texasholdem.THPot;
import poker.game.texasholdem.THTable;
import poker.model.Game;
import poker.model.GameStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("GameEngineRegistry")
@Log4j2
@ToString
public class GameEngineRegistry {
    private final Map<Long, GameEngine> gameEngineMap = new ConcurrentHashMap<>();

    public void registerGame(Game game) {
        Long gameId = game.getId();
        log.info("Registering game id {} with blinds {}/{}", gameId, game.getSmallBlind(), game.getBigBlind());

        var thPot = new THPot(game.getPotId());
//        TODO: restore pot, deck, community cards, players list, player's cards
        var thTable = new THTable(gameId, thPot, GameStatus.getGameStatusByInt(game.getStatus()),
            game.getSmallBlind(), game.getBigBlind());
        GameEngine engine = new THEngine(thTable);

        gameEngineMap.put(gameId, engine);
        log.info("Game id {} registered", gameId);
    }

    public GameEngine getGameEngine(Long gameId) {
        return gameEngineMap.get(gameId);
    }
}
