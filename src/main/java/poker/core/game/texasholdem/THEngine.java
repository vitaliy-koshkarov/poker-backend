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
    public GameState getGameState() {
        return GameStateFactory.create(table);
    }

    @Override
    public void handlePlayerAction(PlayerActionData pad) {
        log.info("Handling {}, player id {}",
            pad.getPlayerAction().getActionName(), pad.getPlayerDetails().getPlayer().getId());

//        TODO: define minRaise value for the next active player
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
        table.setPlayersSeats(snapshot.getPlayersSeats());
    }

    private void startGame() {
        table.startGame();
    }

    private void fold(PlayerActionData pad) {
        table.foldPlayer(pad.getPlayerDetails().getPlayer().getId());
        table.overrideActivePlayer();
    }

    private void check(PlayerActionData pad) {
        table.checkPlayer(pad.getPlayerDetails().getPlayer().getId());
        table.overrideActivePlayer();
    }

    private void bet(PlayerActionData pad) {
        int playerBet = pad.getPlayerBet();
        GamePlayer player = table.getActivePlayers().get(0);
        table.betPlayer(pad.getPlayerDetails().getPlayer().getId(), playerBet);
        table.getPot().addPlayerBet(player, playerBet);
        table.overrideActivePlayer();
    }

    private void allIn(PlayerActionData pad) {
        int playerBet = pad.getPlayerBet();
        GamePlayer player = table.getActivePlayers().get(0);
        table.betPlayer(player.getId(), playerBet);
        table.getPot().addPlayerBet(player, playerBet);
        table.overrideActivePlayer();
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
        log.debug("Player id {} {} game {}", gamePlayer.getId(), pad.getPlayerAction(), pad.getGameId());
    }

    private void disconnectPlayer(PlayerActionData pad) {
        long playerId = pad.getPlayerDetails().getPlayer().getId();
        table.removePlayer(playerId);

//        if (table.getActivePlayerId() == playerId) {
//            table.overrideActivePlayer();
//        }

        log.debug("Player id {} {} game id {}", playerId, pad.getPlayerAction().getActionName(), pad.getGameId());
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
