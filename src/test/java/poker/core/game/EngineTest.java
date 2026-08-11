package poker.core.game;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import poker.core.engine.GameEngine;
import poker.core.game.texasholdem.*;
import poker.core.player.PlayerActionData;
import poker.util.Util;

import static org.junit.jupiter.api.Assertions.*;
import static poker.core.game.GameStatus.*;
import static poker.core.player.PlayerAction.*;
import static poker.core.player.PlayerStatus.*;

@Log4j2
public final class EngineTest {

    @Test
    public void checkCorrectGameCreation() {
        GameTable table = new THTable(1L, "test_game", 1L, 4, 100,
            WAITING_FOR_PLAYERS, 5, 10, new THPot(1L), 1L);
        GameEngine engine = new THEngine(table);

        assertEquals(1L, engine.getGameState().getGameId());
        assertEquals("test_game", engine.getGameState().getName());
        assertEquals(1L, engine.getGameState().getCreatorPlayerId());
        assertEquals(4, engine.getGameState().getMaxPlayers());
        assertEquals(100, engine.getGameState().getBuyIn());
        assertEquals(WAITING_FOR_PLAYERS, engine.getGameState().getGameStatus());
        assertEquals(Util.ZERO_LONG, engine.getGameState().getDealerId());
        assertEquals(Util.ZERO_INT, engine.getGameState().getDealerIndex());
        assertEquals(Util.ZERO_LONG, engine.getGameState().getActivePlayerId());
        assertEquals(5, engine.getGameState().getSmallBlind());
        assertEquals(Util.ZERO_LONG, engine.getGameState().getSmallBlindPlayerId());
        assertEquals(10, engine.getGameState().getBigBlind());
        assertEquals(Util.ZERO_LONG, engine.getGameState().getBigBlindPlayerId());
        assertEquals(Util.ZERO_INT, engine.getGameState().getMinRaise());
        assertEquals(1L, engine.getGameState().getGamePot().getId());
        assertEquals(Util.ZERO_INT, engine.getGameState().getGamePot().getTotal());
        assertTrue(engine.getGameState().getGamePot().getPlayersBets().isEmpty());
        assertTrue(engine.getGameState().getGamePlayers().isEmpty());
        assertEquals(4, engine.getGameState().getPlayersSeats().length);
        assertEquals(1L, engine.getGameState().getRound().getId());
        assertEquals(1L, engine.getGameState().getRound().getGameId());
        assertEquals(Util.ZERO_INT, engine.getGameState().getRound().getLastMaxBet());
        assertEquals(Util.ZERO_LONG, engine.getGameState().getRound().getLastAggressorPlayerId());
        assertTrue(engine.getGameState().getRound().getPlayersToAct().isEmpty());
        assertEquals(52, engine.getGameState().getDeck().getSize());
        assertTrue(engine.getGameState().getCommunityCards().isEmpty());
    }

    @Test
    public void twoPlayersJoinTheGame() {
        long gameId = 1L;
        long playerId1 = 1L;
        GameTable table = new THTable(gameId, "test_game", playerId1, 4, 100,
            WAITING_FOR_PLAYERS, 5, 10, new THPot(1L), 1L);
        GameEngine engine = new THEngine(table);

        PlayerActionData padPlayer1 = THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(JOIN_GAME)
            .userId(1L)
            .playerId(playerId1)
            .nickname("Player_1")
            .chips(Util.ZERO_INT)
            .playerBet(Util.ZERO_INT)
            .dateTimeMs(System.currentTimeMillis())
            .build();

        engine.handlePlayerAction(padPlayer1);

        assertEquals(1, engine.getGameState().getGamePlayers().size());
        assertEquals(JOIN_THE_GAME, engine.getGameState().getGamePlayers().get(0).getStatus());


        long playerId2 = 2L;
        PlayerActionData padPlayer2 = THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(JOIN_GAME)
            .userId(2L)
            .playerId(playerId2)
            .nickname("Player_2")
            .chips(Util.ZERO_INT)
            .playerBet(Util.ZERO_INT)
            .dateTimeMs(System.currentTimeMillis())
            .build();

        engine.handlePlayerAction(padPlayer2);

        assertEquals(2, engine.getGameState().getGamePlayers().size());
        assertEquals(JOIN_THE_GAME, engine.getGameState().getGamePlayers().get(1).getStatus());
    }
}
