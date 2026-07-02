package poker.core.game.texasholdem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poker.core.engine.GameEngine;
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

@RequiredArgsConstructor
@Getter
@Log4j2
public class THEngine implements GameEngine {
    private final GameTable table;

    @Override
    public GameState getCurrentGameState() {
        return GameStateFactory.create(table);
    }

    @Override
    public void handlePlayerAction(PlayerActionData pad) {
        log.info("Handling action {} from player id {}",
            pad.getPlayerAction().getActionName(), pad.getPlayerDetails().getPlayer().getId());

        switch (pad.getPlayerAction()) {
            case START_GAME -> startGame();
            case FOLD -> fold(pad);
            case CHECK -> check(pad);
            case BET -> bet(pad);
            case ALL_IN -> allIn(pad);
            case JOIN_GAME -> joinPlayer(pad);
            case DISCONNECT -> disconnectPlayer(pad);
        }
    }

    @Override
    public GameState snapshot() {
        return GameStateFactory.createSnapshot(table);
    }

    @Override
    public void rollback(GameState snapshot) {
        table.setGameStatus(GameStatus.getGameStatusByInt(snapshot.getGameStatus()));
        table.setDealerId(snapshot.getDealerId());
        table.setDealerIndex(snapshot.getDealerIndex());
        table.setActivePlayerId(snapshot.getActivePlayerId());
        table.setActivePlayerIndex(snapshot.getActivePlayerIndex());
        table.setSmallBlind(snapshot.getSmallBlind());
        table.setSmallBlindIndex(snapshot.getSmallBlindIndex());
        table.setBigBlind(snapshot.getBigBlind());
        table.setBigBlindIndex(snapshot.getBigBlindIndex());
        table.setMinRaise(snapshot.getMinRaise());
        table.setPot(snapshot.getGamePot());
        table.setPlayers(snapshot.getGamePlayers());
        table.setDeck(snapshot.getDeck());
        table.setCommunityCards(snapshot.getCommunityCards());
    }

    private void startGame() {
        table.startGame();
    }

    private void fold(PlayerActionData pad) {
//        todo: update player status
//              define next active player
    }

    private void check(PlayerActionData pad) {
//        todo: update player status
//              define check value (*)
//              define next active player
    }

    private void bet(PlayerActionData pad) {
//        todo: subtract bet from player
//              add player's bet to pot
//              define check or min raise value (*)
//              update player's status
//              define next active player
    }

    private void allIn(PlayerActionData pad) {
//        todo: subtract all chips from player
//              add them to the pot
//              update player's status
//              define next active player
    }

    private void joinPlayer(PlayerActionData pad) {
        GamePlayer gamePlayer = THPlayer.builder()
            .id(pad.getPlayerDetails().getPlayer().getId())
            .nickname(pad.getPlayerDetails().getPlayer().getNickname())
            .status(PlayerStatus.JOIN_THE_GAME)
            .chips(pad.getPlayerDetails().getPlayer().getChips())
            .currentBet(Util.DEFAULT_INT_VALUE)
            .cards(new ArrayList<>())
            .build();

        table.addPlayer(gamePlayer);
        log.info("Player id {} joined the game {}", gamePlayer.getId(), pad.getGameId());
    }

    private void disconnectPlayer(PlayerActionData pad) {
        long playerId = pad.getPlayerDetails().getPlayer().getId();
        table.removePlayer(playerId);

        if (table.getActivePlayerId() == playerId) {
            table.overrideActivePlayer();
        }

        log.info("Player id {} disconnected from game {}", playerId, pad.getGameId());
    }

    private void nextPhase(PlayerActionData pad) {
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
        table.updateGameStatus(PRE_FLOP);
        table.betBlinds();
        table.dealStartHands();
    }

    private void flop() {
        for (int i = 0; i < 3; i++) {
            table.getCommunityCards().add(table.getDeck().dealCard());
        }
        table.updateGameStatus(FLOP);
    }

    private void turn() {
        table.getCommunityCards().add(table.getDeck().dealCard());
        table.updateGameStatus(TURN);
    }

    private void river() {
        table.getCommunityCards().add(table.getDeck().dealCard());
        table.updateGameStatus(RIVER);
    }

    private void showdown() {
        table.updateGameStatus(SHOWDOWN);

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
        table.updateGameStatus(WAITING_FOR_PLAYERS);
    }
}
