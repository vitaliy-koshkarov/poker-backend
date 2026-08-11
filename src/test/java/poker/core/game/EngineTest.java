package poker.core.game;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import poker.core.engine.GameEngine;
import poker.core.game.texasholdem.THEngine;
import poker.core.game.texasholdem.THPot;
import poker.core.game.texasholdem.THTable;

import static org.junit.jupiter.api.Assertions.*;
import static poker.core.game.GameStatus.*;

@Log4j2
public final class EngineTest {

    @Test
    public void checkGameTablePreparation() {
        GameTable table = new THTable(1L, "test_game", 1L, 4, 100,
            WAITING_FOR_PLAYERS, 5, 10, new THPot(1L), 1L);
        GameEngine engine = new THEngine(table);

        GameState gameState = engine.getGameState();
        System.out.println("Game state: " + gameState);
    }
}
