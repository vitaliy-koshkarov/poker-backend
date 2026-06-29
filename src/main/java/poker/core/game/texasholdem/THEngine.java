package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poker.core.engine.GameEngine;
import poker.core.game.GameState;
import poker.core.game.GameStateFactory;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

import java.util.*;

import static poker.core.game.GameStatus.*;

@Log4j2
@RequiredArgsConstructor
public class THEngine implements GameEngine {
    @Getter
    private final GameTable table;

    @Override
    public GameState getCurrentGameState() {
        return GameStateFactory.create(table);
    }

    @Override
    public void handlePlayerAction(long playerId, PlayerActionData playerActionData, PlayerAction playerAction) {
        log.info("Handling action {} from player id {}", playerAction.getActionName(), playerId);

        switch (playerAction) {
            case START_GAME -> startGame(playerActionData);
            case FOLD -> fold(playerActionData);
            case CHECK -> check(playerActionData);
            case BET -> bet(playerActionData);
            case ALL_IN -> allIn(playerActionData);
            case JOIN_GAME -> joinPlayer(playerActionData);
            case DISCONNECT -> disconnectPlayer(playerActionData);
        }
    }

    @Override
    public void rollback(GameState snapshot) {
        log.info("Rollback to {}", snapshot);
    }

    private void startGame(PlayerActionData playerActionData) {
//        todo: update game status
//              define dealer
//              define active player
//              update players' statuses
//              bet blinds (subtract bets from players)
//              add bets to pot
//              deal start hands
    }

    private void fold(PlayerActionData playerActionData) {
//        todo: update player status
//              define next active player
    }

    private void check(PlayerActionData playerActionData) {
//        todo: update player status
//              define check value (*)
//              define next active player
    }

    private void bet(PlayerActionData playerActionData) {
//        todo: subtract bet from player
//              add player's bet to pot
//              define check or min raise value (*)
//              update player's status
//              define next active player
    }

    private void allIn(PlayerActionData playerActionData) {
//        todo: subtract all chips from player
//              add them to the pot
//              update player's status
//              define next active player
    }

    private void joinPlayer(PlayerActionData playerActionData) {
//        todo: add player to the table
    }

    private void disconnectPlayer(PlayerActionData playerActionData) {
//        action depends of the game stage
//        todo: remove player from table
//              define active player
    }

    private void nextPhase(PlayerActionData playerActionData) {
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
        var playersAndCombinations = new HashMap<GamePlayer, HandEvaluator>();
        for (GamePlayer activePlayer : table.getActivePlayers()) {
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

        var winners = new HashMap<GamePlayer, HandEvaluator>();
        for (Map.Entry<GamePlayer, HandEvaluator> pair : playersAndCombinations.entrySet()) {
            if (pair.getValue().getStrength() == strongestCombinationValue) {
                winners.put(pair.getKey(), pair.getValue());
            }
        }

        log.info("Winners {}", winners);
        table.getPot().distributeReward(winners);
//        winners.forEach(Player::takeReward);

//        table.moveDealer();
    }

    private void waitingNewPlayers() {
        table.setGameStatus(WAITING_FOR_PLAYERS);
    }
}
