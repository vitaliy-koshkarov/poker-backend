package poker.core.game;

import org.junit.jupiter.api.Test;
import poker.core.GameEngine;
import poker.core.game.texasholdem.*;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.util.Util;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static poker.core.game.GameStatus.*;
import static poker.util.Util.*;

public final class EngineTest {

    @Test
    public void correctGameCreation() {
        long gameId = 1L;
        String gameName = "test_game";
        int maxPlayers = 4;
        int buyIn = 100;
        int SB = 5;
        int BB = 10;
        THPot pot = new THPot(1L);
        long roundId = 1L;

        long playerId1 = 1L;

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
    public void whenTwoPlayersJoinTheGame() {
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

        GameState gameState = engine.getGameState();

        GamePlayer player1 = gameState.getGamePlayers().get(0);
        assertEquals(1, engine.getGameState().getGamePlayers().size());
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.JOIN_THE_GAME, player1.getStatus());
        assertEquals(INT_ZERO, player1.getChips());
        assertEquals(INT_ZERO, player1.getCurrentBet());
        assertTrue(player1.getCards().isEmpty());


        long pId2 = 2L;
        String p2Name = "P_2";
        PlayerActionData joinP2Action = create(gameId, PlayerAction.JOIN_GAME, pId2, pId2, p2Name, INT_ZERO, INT_ZERO);
        engine.handlePlayerAction(joinP2Action);

        gameState = engine.getGameState();

        GamePlayer player2 = gameState.getGamePlayers().get(1);
        assertEquals(2, gameState.getGamePlayers().size());
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.JOIN_THE_GAME, player2.getStatus());
        assertEquals(INT_ZERO, player2.getChips());
        assertEquals(INT_ZERO, player2.getCurrentBet());
        assertTrue(player2.getCards().isEmpty());
    }

    @Test
    public void startGamePlayerAction() {
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
        assertEquals(SB, gameState.getMinRaise());

        GamePot gamePot = gameState.getGamePot();
        assertEquals(SB + BB, gamePot.getTotal());

        var expectedPlayersBetsMap = new HashMap<Long, Integer>();
        expectedPlayersBetsMap.put(pId1, SB);
        expectedPlayersBetsMap.put(pId2, BB);
        assertEquals(expectedPlayersBetsMap, gamePot.getPlayersBets());

        var gamePlayersList = gameState.getGamePlayers();
        assertEquals(2, gamePlayersList.size());

        GamePlayer player1 = gamePlayersList.get(0);
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.ACTIVE, player1.getStatus());
        assertEquals(buyIn - SB, player1.getChips());
        assertEquals(SB, player1.getCurrentBet());
        assertEquals(2, player1.getCards().size());

        GamePlayer player2 = gamePlayersList.get(1);
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.WAIT, player2.getStatus());
        assertEquals(buyIn - BB, player2.getChips());
        assertEquals(BB, player2.getCurrentBet());
        assertEquals(2, player2.getCards().size());

        long[] playersSeats = gameState.getPlayersSeats();
        assertEquals(2, playersSeats.length);
        assertEquals(pId1, playersSeats[0]);
        assertEquals(pId2, playersSeats[1]);

        THRound round = gameState.getRound();
        assertEquals(roundId, round.getId());
        assertEquals(gameId, round.getGameId());
        assertEquals(INT_ONE, round.getRoundNumber());
        assertEquals(BB, round.getLastMaxBet());
        assertEquals(pId2, round.getLastAggressorPlayerId());

        var playersToActList = round.getPlayersToAct();
        assertEquals(2, playersToActList.size());

        var expectedPlayersToActList = new LinkedList<Long>();
        expectedPlayersToActList.add(pId1);
        expectedPlayersToActList.add(pId2);
        assertEquals(expectedPlayersToActList, playersToActList);

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void foldPlayerAction() {
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
        assertEquals(SB, gameState.getMinRaise());

        GamePot gamePot = gameState.getGamePot();
        assertEquals(SB + BB, gamePot.getTotal());

        var playersBetsMap = new HashMap<Long, Integer>();
        playersBetsMap.put(pId2, SB);
        playersBetsMap.put(pId1, BB);
        assertEquals(playersBetsMap, gamePot.getPlayersBets());

        var gamePlayersList = gameState.getGamePlayers();
        assertEquals(2, gamePlayersList.size());

        GamePlayer player1 = gamePlayersList.get(0);
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.WAIT, player1.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - SB - BB, player1.getChips());
        assertEquals(BB, player1.getCurrentBet());
        assertEquals(2, player1.getCards().size());

        GamePlayer player2 = gamePlayersList.get(1);
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.ACTIVE, player2.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - SB - BB, player2.getChips());
        assertEquals(SB, player2.getCurrentBet());
        assertEquals(2, player2.getCards().size());

        long[] playersSeats = gameState.getPlayersSeats();
        assertEquals(2, playersSeats.length);
        assertEquals(pId1, playersSeats[0]);
        assertEquals(pId2, playersSeats[1]);

        THRound round = gameState.getRound();
        assertEquals(roundId, round.getId());
        assertEquals(gameId, round.getGameId());
        assertEquals(2, round.getRoundNumber());
        assertEquals(pId1, round.getLastAggressorPlayerId());
        assertEquals(BB, round.getLastMaxBet());

        var playersToActList = round.getPlayersToAct();

        var expectedPlayersToActList = new LinkedList<Long>();
        expectedPlayersToActList.add(pId1);
        expectedPlayersToActList.add(pId2);
        assertEquals(expectedPlayersToActList, playersToActList);
        assertEquals(2, playersToActList.size());

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void callPlayerAction() {
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

        PlayerActionData callP1Action = create(gameId, PlayerAction.CALL, pId1, pId1, null, 90, 5);
        engine.handlePlayerAction(callP1Action);

        GameState gameState = engine.getGameState();

        assertEquals(PRE_FLOP, gameState.getGameStatus());
        assertEquals(pId2, gameState.getDealerId());
        assertEquals(1, gameState.getDealerIndex());
        assertEquals(pId2, gameState.getActivePlayerId());
        assertEquals(BB, gameState.getMinRaise());

        GamePot gamePot = gameState.getGamePot();
        assertEquals(BB + BB, gamePot.getTotal());

        var playersBetsMap = new HashMap<Long, Integer>();
        playersBetsMap.put(pId1, BB);
        playersBetsMap.put(pId2, BB);
        assertEquals(playersBetsMap, gamePot.getPlayersBets());

        var gamePlayersList = gameState.getGamePlayers();
        assertEquals(2, gamePlayersList.size());

        GamePlayer player1 = gamePlayersList.get(0);
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.CALL, player1.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - BB, player1.getChips());
        assertEquals(BB, player1.getCurrentBet());
        assertEquals(2, player1.getCards().size());

        GamePlayer player2 = gamePlayersList.get(1);
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.ACTIVE, player2.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - BB, player2.getChips());
        assertEquals(BB, player2.getCurrentBet());
        assertEquals(2, player2.getCards().size());

        long[] playerSeats = gameState.getPlayersSeats();
        assertEquals(2, playerSeats.length);
        assertEquals(pId1, playerSeats[0]);
        assertEquals(pId2, playerSeats[1]);

        THRound round = gameState.getRound();
        assertEquals(roundId, round.getId());
        assertEquals(gameId, round.getGameId());
        assertEquals(INT_ONE, round.getRoundNumber());
        assertEquals(pId2, round.getLastAggressorPlayerId());
        assertEquals(BB, round.getLastMaxBet());

        var playersToActList = round.getPlayersToAct();
        assertEquals(1, playersToActList.size());

        var expectedPlayersToActList = new LinkedList<Long>();
        expectedPlayersToActList.add(pId2);
        assertEquals(expectedPlayersToActList, playersToActList);

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
    }

    @Test
    public void checkPlayerAction() {
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

        PlayerActionData callP1Action = create(gameId, PlayerAction.CALL, pId1, pId1, null, 90, 5);
        engine.handlePlayerAction(callP1Action);

        PlayerActionData checkP2Action = create(gameId, PlayerAction.CHECK, pId2, pId2, null, 90, 0);
        engine.handlePlayerAction(checkP2Action);

        GameState gameState = engine.getGameState();

        assertEquals(FLOP, gameState.getGameStatus());
        assertEquals(pId2, gameState.getDealerId());
        assertEquals(1, gameState.getDealerIndex());
        assertEquals(pId1, gameState.getActivePlayerId());
        assertEquals(BB, gameState.getMinRaise());

        GamePot gamePot = gameState.getGamePot();
        assertEquals(BB + BB, gamePot.getTotal());

        assertTrue(gamePot.getPlayersBets().isEmpty());

        var gamePlayersList = gameState.getGamePlayers();
        assertEquals(2, gamePlayersList.size());

        GamePlayer player1 = gamePlayersList.get(0);
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.ACTIVE, player1.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - BB, player1.getChips());
        assertEquals(INT_ZERO, player1.getCurrentBet());
        assertEquals(2, player1.getCards().size());

        GamePlayer player2 = gamePlayersList.get(1);
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.WAIT, player2.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - BB, player2.getChips());
        assertEquals(INT_ZERO, player2.getCurrentBet());
        assertEquals(2, player2.getCards().size());

        long[] playersSeats = gameState.getPlayersSeats();
        assertEquals(2, playersSeats.length);
        assertEquals(pId1, playersSeats[0]);
        assertEquals(pId2, playersSeats[1]);

        THRound round = gameState.getRound();
        assertEquals(roundId, round.getId());
        assertEquals(gameId, round.getGameId());
        assertEquals(INT_ONE, round.getRoundNumber());
        assertEquals(LONG_ZERO, round.getLastAggressorPlayerId());
        assertEquals(LONG_ZERO, round.getLastMaxBet());

        var playersToActList = round.getPlayersToAct();
        assertEquals(2, playersToActList.size());

        var expectedPlayersToActList = new LinkedList<Long>();
        expectedPlayersToActList.add(pId1);
        expectedPlayersToActList.add(pId2);
        assertEquals(expectedPlayersToActList, playersToActList);

        assertEquals(45, gameState.getDeck().getSize());
        assertEquals(3, gameState.getCommunityCards().size());
    }

    @Test
    public void betPlayerAction() {
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

        PlayerActionData betP1Action = create(gameId, PlayerAction.BET, pId1, pId1, null, 70, 25);
        engine.handlePlayerAction(betP1Action);

        GameState gameState = engine.getGameState();

        assertEquals(PRE_FLOP, gameState.getGameStatus());
        assertEquals(pId2, gameState.getDealerId());
        assertEquals(1, gameState.getDealerIndex());
        assertEquals(pId2, gameState.getActivePlayerId());
        assertEquals(2 * BB, gameState.getMinRaise());

        GamePot gamePot = gameState.getGamePot();
        assertEquals(4 * BB, gamePot.getTotal());

        Map<Long, Integer> playersBetsMap = gamePot.getPlayersBets();
        assertEquals(2, playersBetsMap.size());
        var expectedPlayerBetsMap = new HashMap<Long, Integer>();
        expectedPlayerBetsMap.put(pId1, 3 * BB);
        expectedPlayerBetsMap.put(pId2, BB);
        assertEquals(expectedPlayerBetsMap, playersBetsMap);

        var gamePlayersList = gameState.getGamePlayers();
        assertEquals(2, gamePlayersList.size());

        GamePlayer player1 = gamePlayersList.get(0);
        assertEquals(pId1, player1.getId());
        assertEquals(p1Name, player1.getNickname());
        assertEquals(PlayerStatus.BET, player1.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - 3 * BB, player1.getChips());
        assertEquals(3 * BB, player1.getCurrentBet());
        assertEquals(2, player1.getCards().size());

        GamePlayer player2 = gamePlayersList.get(1);
        assertEquals(pId2, player2.getId());
        assertEquals(p2Name, player2.getNickname());
        assertEquals(PlayerStatus.ACTIVE, player2.getStatus());
//        fixme: chips will change after distribute reward function will be ready
        assertEquals(buyIn - BB, player2.getChips());
        assertEquals(BB, player2.getCurrentBet());
        assertEquals(2, player2.getCards().size());

        long[] playersSeats = gameState.getPlayersSeats();
        assertEquals(2, playersSeats.length);
        assertEquals(pId1, playersSeats[0]);
        assertEquals(pId2, playersSeats[1]);

        THRound round = gameState.getRound();
        assertEquals(roundId, round.getId());
        assertEquals(gameId, round.getGameId());
        assertEquals(INT_ONE, round.getRoundNumber());
        assertEquals(pId1, round.getLastAggressorPlayerId());
        assertEquals(3 * BB, round.getLastMaxBet());

        var playersToActList = round.getPlayersToAct();
        assertEquals(1, playersToActList.size());

        var expectedPlayersToActList = new LinkedList<Long>();
        expectedPlayersToActList.add(pId2);
        assertEquals(expectedPlayersToActList, playersToActList);

        assertEquals(48, gameState.getDeck().getSize());
        assertTrue(gameState.getCommunityCards().isEmpty());
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
