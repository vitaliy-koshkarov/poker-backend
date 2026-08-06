package poker.core.game.texasholdem;

import lombok.extern.log4j.Log4j2;
import poker.core.engine.GameEngine;
import poker.core.game.GameState;
import poker.core.game.GameStateFactory;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
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
        table.setGameStatus(snapshot.getGameStatus());
        table.setDealerId(snapshot.getDealerId());
        table.setDealerIndex(snapshot.getDealerIndex());
        table.setActivePlayerId(snapshot.getActivePlayerId());
        table.setSmallBlind(snapshot.getSmallBlind());
        table.setSmallBlindPlayerId(snapshot.getSmallBlindPlayerId());
        table.setBigBlind(snapshot.getBigBlind());
        table.setBigBlindPlayerId(snapshot.getBigBlindPlayerId());
        table.setLastMaxBet(snapshot.getLastMaxBet());
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

    private void startGame() {
        table.startGame();
    }

    private void disconnectPlayer(PlayerActionData pad) {
        long playerId = pad.getPlayerId();
        table.removePlayer(playerId);

//        if (table.getActivePlayerId() == playerId) {
//            table.overrideActivePlayer();
//        }

        log.debug("Player id {} {} game id {}", playerId, pad.getPlayerAction().getActionName(), pad.getGameId());
    }

    private void fold(PlayerActionData pad) {
        table.foldPlayer(pad.getPlayerId());
        table.defineNewActivePlayer();
        table.defineMinRaise();
    }

    private void check(PlayerActionData pad) {
        table.checkPlayer(pad.getPlayerId());
        table.defineNewActivePlayer();
        table.defineMinRaise();
    }

    private void bet(PlayerActionData pad) {
        int playerBet = pad.getPlayerBet();
        long activePlayerId = table.getActivePlayer().getId();
        table.betPlayer(activePlayerId, playerBet);
        table.getPot().addPlayerBet(activePlayerId, playerBet);
        table.defineNewActivePlayer();
        table.defineMinRaise();
    }

    private void allIn(PlayerActionData pad) {
        int playerBet = pad.getPlayerBet();
        long activePlayerId = table.getActivePlayer().getId();
        table.betPlayer(activePlayerId, playerBet);
        table.getPot().addPlayerBet(activePlayerId, playerBet);
        table.defineNewActivePlayer();
        table.defineMinRaise();
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
        for (GamePlayer activePlayer : table.getPlayers()) {
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
