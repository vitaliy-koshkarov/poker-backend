package poker.core.game.texasholdem;

import lombok.extern.log4j.Log4j2;
import poker.core.GameEngine;
import poker.core.game.GameState;
import poker.core.game.GameStateFactory;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.util.Util;

import java.util.*;

import static poker.core.game.GameStatus.*;
import static poker.util.Util.*;

@Log4j2
public record THEngine(GameTable table) implements GameEngine {

    @Override
    public GameState getGameState() {
        return GameStateFactory.create(table);
    }

    @Override
    public void handlePlayerAction(PlayerActionData pad) {
        log.info("Handle {} player id {}", pad.getPlayerAction().getActionName(), pad.getPlayerId());

        switch (pad.getPlayerAction()) {
            case JOIN_GAME -> joinPlayer(pad);
            case DISCONNECT -> disconnectPlayer(pad);
            case START_GAME -> startGame(true);
            case FOLD -> fold(pad);
            case CHECK -> check(pad);
            case CALL -> call(pad);
            case BET -> bet(pad);
            case RAISE -> raise(pad);
            case ALL_IN -> allIn(pad);
        }

//        TODO: broadcast winners with the same new round frame

        GameStatus gameStatus = table.getGameStatus();
        if (isGameNotEnded(gameStatus) && isAllPlayersFoldExceptOne()) {
            distributeRewardForOneWinner();
            startGame(false);
            return;
        }

        if (RIVER.equals(gameStatus) && table.getRound().getPlayersToAct().isEmpty()) {
            Map<Long, HandEvaluator> playersAndCombinations = evaluateHands();
            determineWinners(playersAndCombinations);
            distributeReward();
            startGame(false);
            return;
        }

        if (!WAITING_FOR_PLAYERS.equals(gameStatus) && !END.equals(gameStatus) && isCurrentRoundEnded()) {
            nextStage();
        }
    }

    @Override
    public GameState snapshot() {
        return GameStateFactory.createSnapshot(table);
    }

    @Override
    public void rollback(GameState snapshot) {
        table.setGameStatus(snapshot.getGameStatus());
        table.setDealerId(snapshot.getDealerId());
        table.setDealerIndex(snapshot.getDealerIndex());
        table.setActivePlayerId(snapshot.getActivePlayerId());
        table.setSmallBlind(snapshot.getSmallBlind());
        table.setSmallBlindPlayerId(snapshot.getSmallBlindPlayerId());
        table.setBigBlind(snapshot.getBigBlind());
        table.setBigBlindPlayerId(snapshot.getBigBlindPlayerId());
        table.setMinRaise(snapshot.getMinRaise());
        table.setPot(snapshot.getGamePot());

        var playersMap = new HashMap<Long, GamePlayer>();
        for (GamePlayer gamePlayer : snapshot.getGamePlayers()) {
            playersMap.put(gamePlayer.getId(), gamePlayer);
        }
        table.setPlayersMap(playersMap);
        table.setPlayersSeats(snapshot.getPlayersSeats());

        table.setRound(snapshot.getRound());

        table.setDeck(snapshot.getDeck());
        table.setCommunityCards(snapshot.getCommunityCards());
    }

    private void joinPlayer(PlayerActionData pad) {
        GamePlayer gamePlayer = THPlayer.builder()
            .id(pad.getPlayerId())
            .nickname(pad.getNickname())
            .status(PlayerStatus.JOIN_THE_GAME)
            .chips(pad.getChips())
            .currentBet(Util.INT_ZERO)
            .cards(new ArrayList<>())
            .build();

        table.addPlayer(gamePlayer);
        log.debug("Player id {} {} game {}", gamePlayer.getId(), pad.getPlayerAction(), pad.getGameId());
    }

    private void disconnectPlayer(PlayerActionData pad) {
        long playerId = pad.getPlayerId();
        table.removePlayer(playerId);

//        todo: if player disconnects, wait few seconds, then pass the turn to the next player

        log.debug("Player id {} {} game id {}", playerId, pad.getPlayerAction().getActionName(), pad.getGameId());
    }

    private void startGame(boolean isFirstRound) {
        if (isFirstRound) {
            for (GamePlayer player : table.getPlayers()) {
                player.setChips(table.getBuyIn());
            }
        }

        table.startNewRound();
    }

    private void fold(PlayerActionData pad) {
        table.foldPlayer(pad.getPlayerId());
    }

    private void check(PlayerActionData pad) {
        table.checkPlayer(pad.getPlayerId());
    }

    private void call(PlayerActionData pad) {
        table.call(pad.getPlayerId(), pad.getPlayerBet());
    }

    private void bet(PlayerActionData pad) {
        table.betPlayer(pad.getPlayerId(), pad.getPlayerBet());
    }

    private void raise(PlayerActionData pad) {
        table.raise(pad.getPlayerId(), pad.getPlayerBet());
    }

    private void allIn(PlayerActionData pad) {
        table.betPlayer(pad.getPlayerId(), pad.getPlayerBet());
    }

    private boolean isCurrentRoundEnded() {
        return table.getRound().getPlayersToAct().isEmpty();
    }

    private void nextStage() {
        switch (table.getGameStatus()) {
            case WAITING_FOR_PLAYERS -> preFlop();
            case PRE_FLOP -> flop();
            case FLOP -> turn();
            case TURN -> river();
            case RIVER -> showdown();
        }
    }

    private void preFlop() {
        table.preFlop();
    }

    private void flop() {
        table.flop();
    }

    private void turn() {
        table.turn();
    }

    private void river() {
        table.river();
    }

    private void showdown() {
        table.setGameStatus(SHOWDOWN);
//        todo: evaluate hands, determine winners and distribute reward
        evaluateHandsAndDistributeReward();

        table.showdown();
    }

    private void evaluateHandsAndDistributeReward() {
//        TODO: improve logic of evaluating hands and splitting pot between players
//        var playersAndCombinations = new HashMap<GamePlayer, HandEvaluator>();
//        for (GamePlayer activePlayer : table.getPlayers()) {
//            var cards = new ArrayList<Card>();
//            cards.addAll(table.getCommunityCards());
//            cards.addAll(activePlayer.getCards());
//
//            playersAndCombinations.put(activePlayer, HandEvaluator.evaluate(cards));
//        }
//
//        playersAndCombinations.forEach((player, handEvaluator) -> {
//            log.info("Player {}", player);
//            log.info("HandEvaluator {}", handEvaluator);
//        });
//
//        int strongestCombinationValue = 0;
//        for (HandEvaluator hand : playersAndCombinations.values()) {
//            if (hand.getStrength() > strongestCombinationValue) {
//                strongestCombinationValue = hand.getStrength();
//            }
//        }
//
//        var winners = new HashMap<GamePlayer, HandEvaluator>();
//        for (Map.Entry<GamePlayer, HandEvaluator> pair : playersAndCombinations.entrySet()) {
//            if (pair.getValue().getStrength() == strongestCombinationValue) {
//                winners.put(pair.getKey(), pair.getValue());
//            }
//        }
//
//        log.info("Winners {}", winners);
//        table.getPot().distributeReward(winners);
//        winners.forEach(Player::takeReward);

//        table.moveDealer();
    }

    private boolean isGameNotEnded(GameStatus gS) {
        return PRE_FLOP.equals(gS) || FLOP.equals(gS) || TURN.equals(gS) || RIVER.equals(gS);
    }

    private boolean isAllPlayersFoldExceptOne() {
        int foldPlayersCounter = INT_ZERO;
        for (GamePlayer p : table.getPlayers()) {
            if (PlayerStatus.FOLD.equals(p.getStatus()))
                foldPlayersCounter++;
        }
        return foldPlayersCounter == INT_ONE;
    }

    private void distributeRewardForOneWinner() {
        long winnerPlayerId = LONG_ZERO;
        for (GamePlayer p : table.getPlayers()) {
            if (!PlayerStatus.FOLD.equals(p.getStatus())) {
                winnerPlayerId = p.getId();
                break;
            }
        }

        int chipsReward = table.getPot().getTotal();
        table.getPot().refresh();

        GamePlayer winnerGamePlayer = table.getPlayerById(winnerPlayerId);
        winnerGamePlayer.takeReward(chipsReward);
        winnerGamePlayer.setCurrentBet(INT_ZERO);

        var winnerCardsList = new ArrayList<Card>();
        winnerCardsList.addAll(table.getPlayerById(winnerPlayerId).getCards());
        winnerCardsList.addAll(table.getCommunityCards());

        WinnerPlayer winnerPlayer = new WinnerPlayer(winnerGamePlayer.getId(), chipsReward, winnerCardsList);
        table.setLastRoundWinnerPlayer(winnerPlayer);
    }

    private Map<Long, HandEvaluator> evaluateHands() {
//        TODO: re-write algorithm of evaluation hands
        var playersAndCombinations = new HashMap<Long, HandEvaluator>();
        for (GamePlayer player : table.getPlayers()) {
            if (PlayerStatus.CHECK.equals(player.getStatus())
                || PlayerStatus.CALL.equals(player.getStatus())
                || PlayerStatus.ALL_IN.equals(player.getStatus())) {
                var cards = new ArrayList<Card>();
                cards.addAll(table.getCommunityCards());
                cards.addAll(player.getCards());

                playersAndCombinations.put(player.getId(), HandEvaluator.evaluate(cards));
            }
        }
        return playersAndCombinations;
    }

    private void determineWinners(Map<Long, HandEvaluator> playersAndCombinations) {
//        TODO: implement for several winners
        int strongestCombination = INT_ZERO;
        long winnerPlayerId =  LONG_ZERO;
        for (Map.Entry<Long, HandEvaluator> pair : playersAndCombinations.entrySet()) {
            if (pair.getValue().getStrength() > strongestCombination) {
                strongestCombination = pair.getValue().getStrength();
                winnerPlayerId = pair.getKey();
            }
        }

        var winnerCardsList = new ArrayList<Card>();
        winnerCardsList.addAll(table.getPlayerById(winnerPlayerId).getCards());
        winnerCardsList.addAll(table.getCommunityCards());

        WinnerPlayer winnerPlayer = new WinnerPlayer(winnerPlayerId, table.getPot().getTotal(), winnerCardsList);
        table.setLastRoundWinnerPlayer(winnerPlayer);
    }

    private void distributeReward() {
//        TODO: implement logic for reward distribution between one or several winners
        WinnerPlayer winnerPlayer = table.getLastRoundWinnerPlayer();

        GamePlayer winnerGamePlayer = table.getPlayerById(winnerPlayer.id());
        winnerGamePlayer.takeReward(table.getPot().getTotal());
        winnerGamePlayer.setCurrentBet(INT_ZERO);

        table.getPot().refresh();
    }
}
