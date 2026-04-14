package poker.game.texasholdem;

import lombok.extern.log4j.Log4j2;
import poker.game.GameEngine;
import poker.game.GameState;
import poker.game.PlayerAction;
import poker.model.Game;
import poker.model.Player;
import poker.model.event.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static poker.model.GameStatus.*;

@Log4j2
public class THEngine implements GameEngine {
    private final THTable table;

    public THEngine(THTable table) {
        this.table = table;
    }

    @Override
    public GameEvent handleAction(PlayerAction action, long playerId, Game game, List<Player> players) {
//        TODO: implement logic

        if (PlayerAction.START_GAME.equals(action)) {
            newGame();
        } else if (PlayerAction.CHECK.equals(action)) {
        } else if (PlayerAction.BET.equals(action)) {
        } else if (PlayerAction.ALL_IN.equals(action)) {
        } else if (PlayerAction.FOLD.equals(action)) {
        }

        var gameEvent = GameEvent.builder().build();
        log.info("Handled action {} from player {}", action, playerId);
        return gameEvent;
    }

    public GameState getGameState() {
//        TODO: implement logic
        return null;
    }

    private void newGame() {
        table.startNewGame();
        preFlop();
    }

    private void nextPhase() {
        switch (table.getGameStatus()) {
            case WAITING_FOR_PLAYERS -> preFlop();
            case PRE_FLOP -> flop();
            case FLOP -> turn();
            case TURN -> river();
            case RIVER -> showdown();
            case SHOWDOWN -> waitingNewPlayers();
        }
    }

    private void preFlop() {
        table.setGameStatus(PRE_FLOP);
        table.betBlinds();
        table.defineCurrentPlayer();
        table.dealStartHands();
    }

    private void flop() {
        for (int i = 0; i < 3; i++) {
            table.getCommunityCards().add(table.getDeck().dealCard());
        }
        table.setGameStatus(FLOP);
    }

    private void turn() {
        table.getCommunityCards().add(table.getDeck().dealCard());
        table.setGameStatus(TURN);
    }

    private void river() {
        table.getCommunityCards().add(table.getDeck().dealCard());
        table.setGameStatus(RIVER);
    }

    private void showdown() {
        table.setGameStatus(SHOWDOWN);

//        TODO: improve logic of evaluating hands and splitting pot between players
        var playersAndCombinations = new HashMap<THPlayer, HandEvaluator>();
        for (THPlayer activePlayer : table.getActivePlayers()) {
            var cards = new ArrayList<Card>();
            cards.addAll(table.getCommunityCards());
            cards.addAll(activePlayer.getCards());

            playersAndCombinations.put(activePlayer, HandEvaluator.evaluate(cards));
        }

        playersAndCombinations.forEach((player, handEvaluator) -> {
            log.info("Player {}", player);
            log.info("HandEvaluator {}", handEvaluator);
        });

        int strongestCombinationValue = 0;
        for (HandEvaluator hand : playersAndCombinations.values()) {
            if (hand.getStrength() > strongestCombinationValue) {
                strongestCombinationValue = hand.getStrength();
            }
        }

        var winners = new HashMap<THPlayer, HandEvaluator>();
        for (Map.Entry<THPlayer, HandEvaluator> pair : playersAndCombinations.entrySet()) {
            if (pair.getValue().getStrength() == strongestCombinationValue) {
                winners.put(pair.getKey(), pair.getValue());
            }
        }

        log.info("Winners {}", winners);
        table.getPot().distributeReward(winners);
//        winners.forEach(Player::takeReward);

        table.moveDealer();
    }

    private void waitingNewPlayers() {
        table.setGameStatus(WAITING_FOR_PLAYERS);
    }
}
