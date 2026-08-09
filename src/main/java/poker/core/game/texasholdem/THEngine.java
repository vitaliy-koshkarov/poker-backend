package poker.core.game.texasholdem;

import lombok.extern.log4j.Log4j2;
import poker.core.engine.GameEngine;
import poker.core.game.GameState;
import poker.core.game.GameStateFactory;
import poker.core.game.GameTable;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.core.player.PlayerStatus;
import poker.util.Util;

import java.util.*;

import static poker.core.game.GameStatus.*;

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
            case START_GAME -> startGame();
            case FOLD -> fold(pad);
            case CHECK -> check(pad);
            case CALL -> call(pad);
            case BET -> bet(pad);
            case RAISE -> raise(pad);
            case ALL_IN -> allIn(pad);
        }

        if (isCurrentRoundEnded()) {
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

        table.setDeck(snapshot.getDeck());
        table.setCommunityCards(snapshot.getCommunityCards());
        table.setPlayersSeats(snapshot.getPlayersSeats());
        table.setBettingRound(snapshot.getRound());
    }

    private void joinPlayer(PlayerActionData pad) {
        GamePlayer gamePlayer = THPlayer.builder()
            .id(pad.getPlayerId())
            .nickname(pad.getNickname())
            .status(PlayerStatus.JOIN_THE_GAME)
            .chips(pad.getChips())
            .currentBet(Util.ZERO_INT)
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

    private void startGame() {
        table.startGame();
    }

    private void fold(PlayerActionData pad) {
        table.foldPlayer(pad.getPlayerId());
    }

    private void check(PlayerActionData pad) {
        table.checkPlayer(pad.getPlayerId());
    }

    private void call(PlayerActionData pad) {
//        todo: implement
    }

    private void bet(PlayerActionData pad) {
        table.betPlayer(pad.getPlayerId(), pad.getPlayerBet());
    }

    private void raise(PlayerActionData pad) {
//        todo: implement
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
}
