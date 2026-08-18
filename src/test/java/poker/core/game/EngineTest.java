package poker.core.game;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import poker.core.engine.GameEngine;
import poker.core.game.texasholdem.*;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.util.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;
import static poker.core.game.GameStatus.*;
import static poker.util.Util.*;

@Log4j2
public final class EngineTest {

    @Test
    public void testCorrectGameCreation() {
        long gameId = 1L;
        String gameName = "test_game";
        long playerId1 = 1L;
        int maxPlayers = 4;
        int buyIn = 100;
        int SB = 5;
        int BB = 10;
        THPot pot = new THPot(1L);
        long roundId = 1L;

        GameTable table = new THTable(gameId, gameName, playerId1, maxPlayers, buyIn, WAITING_FOR_PLAYERS, SB, BB, pot, roundId);
        GameEngine engine = new THEngine(table);

        GameState gameState = engine.getGameState();

        assertEquals(gameId, gameState.getGameId());
        assertEquals(gameName, gameState.getName());
        assertEquals(playerId1, gameState.getCreatorPlayerId());
        assertEquals(maxPlayers, gameState.getMaxPlayers());
        assertEquals(buyIn, gameState.getBuyIn());
        assertEquals(WAITING_FOR_PLAYERS, gameState.getGameStatus());
        assertEquals(Util.LONG_ZERO, gameState.getDealerId());
        assertEquals(INT_ZERO, gameState.getDealerIndex());
        assertEquals(Util.LONG_ZERO, gameState.getActivePlayerId());
        assertEquals(SB, gameState.getSmallBlind());
        assertEquals(Util.LONG_ZERO, gameState.getSmallBlindPlayerId());
        assertEquals(BB, gameState.getBigBlind());
        assertEquals(Util.LONG_ZERO, gameState.getBigBlindPlayerId());
        assertEquals(INT_ZERO, gameState.getMinRaise());
        assertEquals(pot.getId(), gameState.getGamePot().getId());
        assertEquals(INT_ZERO, gameState.getGamePot().getTotal());
        assertTrue(gameState.getGamePot().getPlayersBets().isEmpty());
        assertTrue(gameState.getGamePlayers().isEmpty());
        assertEquals(maxPlayers, gameState.getPlayersSeats().length);
        assertEquals(roundId, gameState.getRound().getId());
        assertEquals(gameId, gameState.getRound().getGameId());
        assertEquals(INT_ZERO, gameState.getRound().getLastMaxBet());
        assertEquals(Util.LONG_ZERO, gameState.getRound().getLastAggressorPlayerId());
        assertTrue(gameState.getRound().getPlayersToAct().isEmpty());
        assertEquals(TEXAS_HOLDEM_DECK_SIZE, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void twoPlayersJoinTheGame() {
        long gameId = 1L;
        String gameName = "test_game";
        int maxPlayers = 2;
        int buyIn = 100;
        int SB = 5;
        int BB = 10;
        THPot pot = new THPot(1L);
        long roundId = 1L;

        long pId1 = 1L;
        String p1Name = "P_1";

        GameTable table = new THTable(gameId, gameName, pId1, maxPlayers, buyIn, WAITING_FOR_PLAYERS, SB, BB, pot, roundId);
        GameEngine engine = new THEngine(table);

        PlayerActionData joinP1Action = create(gameId, PlayerAction.JOIN_GAME, pId1, pId1, p1Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP1Action);

        assertEquals(1, engine.getGameState().getGamePlayers().size());
        assertEquals(pId1, engine.getGameState().getGamePlayers().get(0).getId());
        assertEquals(p1Name, engine.getGameState().getGamePlayers().get(0).getNickname());
        assertEquals(PlayerStatus.JOIN_THE_GAME, engine.getGameState().getGamePlayers().get(0).getStatus());
        assertEquals(INT_ZERO, engine.getGameState().getGamePlayers().get(0).getChips());
        assertEquals(INT_ZERO, engine.getGameState().getGamePlayers().get(0).getCurrentBet());
        assertTrue(engine.getGameState().getGamePlayers().get(0).getCards().isEmpty());


        long pId2 = 2L;
        String p2Name = "P_2";
        PlayerActionData joinP2Action = create(gameId, PlayerAction.JOIN_GAME, pId2, pId2, p2Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP2Action);

        assertEquals(2, engine.getGameState().getGamePlayers().size());
        assertEquals(pId2, engine.getGameState().getGamePlayers().get(1).getId());
        assertEquals(p2Name, engine.getGameState().getGamePlayers().get(1).getNickname());
        assertEquals(PlayerStatus.JOIN_THE_GAME, engine.getGameState().getGamePlayers().get(1).getStatus());
        assertEquals(INT_ZERO, engine.getGameState().getGamePlayers().get(1).getChips());
        assertEquals(INT_ZERO, engine.getGameState().getGamePlayers().get(1).getCurrentBet());
        assertTrue(engine.getGameState().getGamePlayers().get(1).getCards().isEmpty());
    }

    @Test
    public void correctGameStateAtStartOfGameFor2Players() {
        long gameId = 1L;
        String gameName = "test_game";
        int maxPlayers = 2;
        int buyIn = 100;
        int SB = 5;
        int BB = 10;
        THPot pot = new THPot(1L);
        long roundId = 1L;

        long pId1 = 1L;
        String p1Name = "P_1";
        GameTable table = new THTable(gameId, gameName, pId1, maxPlayers, buyIn, WAITING_FOR_PLAYERS, SB, BB, pot, roundId);
        GameEngine engine = new THEngine(table);

        PlayerActionData joinP1Action = create(gameId, PlayerAction.JOIN_GAME, pId1, pId1, p1Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP1Action);


        long pId2 = 2L;
        String p2Name = "P_2";
        PlayerActionData joinP2Action = create(gameId, PlayerAction.JOIN_GAME, pId2, pId2, p2Name, INT_ZERO, INT_ZERO);;
        engine.handlePlayerAction(joinP2Action);


        PlayerActionData startGameAction = create(gameId, PlayerAction.START_GAME, pId1, pId1, null, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(startGameAction);

        GameState gameState = engine.getGameState();

        assertEquals(PRE_FLOP, gameState.getGameStatus());
        assertEquals(pId2, gameState.getDealerId());
        assertEquals(1, gameState.getDealerIndex());
        assertEquals(pId1, gameState.getActivePlayerId());
        assertEquals(pId1, gameState.getSmallBlindPlayerId());
        assertEquals(pId2, gameState.getBigBlindPlayerId());
        assertEquals(BB, gameState.getMinRaise());

        assertEquals(SB + BB, gameState.getGamePot().getTotal());

        var expectedPlayersBetsMap = new HashMap<Long, Integer>();
        expectedPlayersBetsMap.put(pId1, SB);
        expectedPlayersBetsMap.put(pId2, BB);
        assertEquals(expectedPlayersBetsMap, gameState.getGamePot().getPlayersBets());

        assertEquals(2, gameState.getGamePlayers().size());

        assertEquals(pId1, gameState.getGamePlayers().get(0).getId());
        assertEquals(p1Name, gameState.getGamePlayers().get(0).getNickname());
        assertEquals(PlayerStatus.ACTIVE, gameState.getGamePlayers().get(0).getStatus());
        assertEquals(buyIn - SB, gameState.getGamePlayers().get(0).getChips());
        assertEquals(SB, gameState.getGamePlayers().get(0).getCurrentBet());
        assertEquals(2, gameState.getGamePlayers().get(0).getCards().size());

        assertEquals(pId2, gameState.getGamePlayers().get(1).getId());
        assertEquals(p2Name, gameState.getGamePlayers().get(1).getNickname());
        assertEquals(PlayerStatus.WAIT, gameState.getGamePlayers().get(1).getStatus());
        assertEquals(buyIn - BB, gameState.getGamePlayers().get(1).getChips());
        assertEquals(BB, gameState.getGamePlayers().get(1).getCurrentBet());
        assertEquals(2, gameState.getGamePlayers().get(1).getCards().size());

        assertEquals(2, gameState.getPlayersSeats().length);
        assertEquals(pId1, gameState.getPlayersSeats()[0]);
        assertEquals(pId2, gameState.getPlayersSeats()[1]);
        assertEquals(roundId, gameState.getRound().getId());
        assertEquals(gameId, gameState.getRound().getGameId());
        assertEquals(BB, gameState.getRound().getLastMaxBet());
        assertEquals(pId2, gameState.getRound().getLastAggressorPlayerId());
        assertEquals(2, gameState.getRound().getPlayersToAct().size());

        var playersToActSet = new LinkedHashSet<Long>();
        playersToActSet.add(pId1);
        playersToActSet.add(pId2);
        assertEquals(playersToActSet, gameState.getRound().getPlayersToAct());

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void testFoldActionIn1RoundFor2Players() {
        long gameId = 1L;
        String gameName = "test_game";
        int maxPlayers = 2;
        int buyIn = 100;
        int SB = 5;
        int BB = 10;
        THPot pot = new THPot(1L);
        long roundId = 1L;

        long pId1 = 1L;
        String p1Name = "P_1";
        GameTable table = new THTable(gameId, gameName, pId1, maxPlayers, buyIn, WAITING_FOR_PLAYERS, SB, BB, pot, roundId);
        GameEngine engine = new THEngine(table);

        PlayerActionData joinP1Action = create(gameId, PlayerAction.JOIN_GAME, pId1, pId1, p1Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP1Action);

        long pId2 = 2L;
        String p2Name = "P_2";
        PlayerActionData joinP2Action = create(gameId, PlayerAction.JOIN_GAME, pId2, pId2, p2Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP2Action);


        PlayerActionData startGameAction = create(gameId, PlayerAction.START_GAME, pId1, pId1, null, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(startGameAction);


        PlayerActionData foldP1Action = create(gameId, PlayerAction.FOLD, pId1, pId1, null, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(foldP1Action);


        GameState gameState = engine.getGameState();

        assertEquals(PRE_FLOP, gameState.getGameStatus());
        assertEquals(pId1, gameState.getDealerId());
        assertEquals(INT_ZERO, gameState.getDealerIndex());
        assertEquals(pId2, gameState.getActivePlayerId());
        assertEquals(BB, gameState.getMinRaise());
        assertEquals(SB + BB, gameState.getGamePot().getTotal());

        var playersBetsMap = new HashMap<Long, Integer>();
        playersBetsMap.put(pId2, SB);
        playersBetsMap.put(pId1, BB);
        assertEquals(playersBetsMap, gameState.getGamePot().getPlayersBets());

        assertEquals(2, gameState.getGamePlayers().size());

        assertEquals(pId1, gameState.getGamePlayers().get(0).getId());
        assertEquals(p1Name, gameState.getGamePlayers().get(0).getNickname());
        assertEquals(PlayerStatus.WAIT, gameState.getGamePlayers().get(0).getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - SB - BB, gameState.getGamePlayers().get(0).getChips());
        assertEquals(BB, gameState.getGamePlayers().get(0).getCurrentBet());
        assertEquals(2, gameState.getGamePlayers().get(0).getCards().size());

        assertEquals(pId2, gameState.getGamePlayers().get(1).getId());
        assertEquals(p2Name, gameState.getGamePlayers().get(1).getNickname());
        assertEquals(PlayerStatus.ACTIVE, gameState.getGamePlayers().get(1).getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - SB - BB, gameState.getGamePlayers().get(1).getChips());
        assertEquals(SB, gameState.getGamePlayers().get(1).getCurrentBet());
        assertEquals(2, gameState.getGamePlayers().get(1).getCards().size());

        assertEquals(2, gameState.getPlayersSeats().length);
        assertEquals(pId1, gameState.getPlayersSeats()[0]);
        assertEquals(pId2, gameState.getPlayersSeats()[1]);

        assertEquals(pId1, gameState.getRound().getLastAggressorPlayerId());
        assertEquals(BB, gameState.getRound().getLastMaxBet());

        var playersToActSet = new HashSet<Long>();
        playersToActSet.add(pId1);
        playersToActSet.add(pId2);
        assertEquals(playersToActSet, gameState.getRound().getPlayersToAct());

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void checkCallPlayerAction() {
//        long gameId = 1L;
//        String gameName = "test_game";
//        long playerId1 = 1L;
//        int maxPlayers = 2;
//        int buyIn = 100;
//        int SB = 5;
//        int BB = 10;
//        THPot pot = new THPot(1L);
//        long roundId = 1L;
//        GameTable table = new THTable(gameId, gameName, playerId1, maxPlayers, buyIn, WAITING_FOR_PLAYERS, SB, BB, pot, roundId);
//        GameEngine engine = new THEngine(table);
//
//        PlayerActionData join1PlayerAction = create(gameId, PlayerAction.JOIN_GAME, playerId1, playerId1, "P_1", ZERO_INT, ZERO_INT);
//        engine.handlePlayerAction(join1PlayerAction);
//
//        long playerId2 = 2L;
//        PlayerActionData join2PlayerAction = create(gameId, PlayerAction.JOIN_GAME, playerId2, playerId2, "P_2", ZERO_INT, ZERO_INT);
//        engine.handlePlayerAction(join2PlayerAction);
//
//
//        PlayerActionData startGameAction = create(gameId, PlayerAction.START_GAME, playerId1, playerId1, null, ZERO_INT, ZERO_INT);
//        engine.handlePlayerAction(startGameAction);
//
//
//        PlayerActionData call1PlayerAction = create(gameId, PlayerAction.CALL, playerId1, playerId1, null, 95, 5);
//        engine.handlePlayerAction(call1PlayerAction);
//
//
//        GameState gameState = engine.getGameState();
//
//        assertEquals(PRE_FLOP, gameState.getGameStatus());
//        assertEquals(playerId2, gameState.getDealerId());
//        assertEquals(1, gameState.getDealerIndex());
//        assertEquals(playerId2, gameState.getActivePlayerId());
//        assertEquals(BB, gameState.getMinRaise());
//        assertEquals(BB + BB, gameState.getGamePot().getTotal());
//
//        var playersBetsMap = new HashMap<Long, Integer>();
//        playersBetsMap.put(playerId1, BB);
//        playersBetsMap.put(playerId2, BB);
//        assertEquals(playersBetsMap, gameState.getGamePot().getPlayersBets());
//
//        assertEquals(2, gameState.getGamePlayers().size());
//        assertEquals(2, gameState.getPlayersSeats().length);
//        assertEquals(playerId1, gameState.getPlayersSeats()[0]);
//        assertEquals(playerId2, gameState.getPlayersSeats()[1]);
//
//        assertEquals(playerId2, gameState.getRound().getLastAggressorPlayerId());
//        assertEquals(BB, gameState.getRound().getLastMaxBet());
//
//        var playersToActSet = new HashSet<Long>();
//        playersToActSet.add(playerId2);
//        assertEquals(playersToActSet, gameState.getRound().getPlayersToAct());
//
//        assertEquals(48, gameState.getDeck().getSize());
//        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    private PlayerActionData create(long gameId, PlayerAction playerAction, long userId, long playerId, String nickname,
                                    int playerChips, int playerBet) {
        return THPlayerActionData.builder()
            .gameId(gameId)
            .playerAction(playerAction)
            .userId(userId)
            .playerId(playerId)
            .nickname(nickname)
            .chips(playerChips)
            .playerBet(playerBet)
            .dateTimeMs(System.currentTimeMillis())
            .build();
    }
}
