package poker.game;

import lombok.extern.log4j.Log4j2;
import poker.model.*;
import poker.model.event.EventData;
import poker.model.event.GameEvent;
import poker.util.Util;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Log4j2
public class GameEngineStub implements GameEngine {
    private GameState gameState;

    @Override
    public GameEvent handleAction(Game game, Long playerId, Long dealerId, Long activePlayerId,
                                  List<Player> players, List<Object> communityCards, PlayerAction action, Pot pot) {
        log.info("Handle action {} player id {}", action, playerId);

        var playerStaus = PlayerStatus.IN_GAME.getStatus();
        var chips = game.getBuyIn();
        for (Player player : players) {
            player.setStatus(playerStaus);
            player.setChips(chips);
//            TODO: set current bet evaluated by engine
        }

//        TODO: set dealer id and active player id evaluated by engine
        var now = new Timestamp(System.currentTimeMillis());
        game.setStatus(GameStatus.START.getStatus());
        game.setStartedAt(now);
        game.setDealerId(Util.DEFAULT_LONG_VALUE);
        game.setActivePlayerId(Util.DEFAULT_LONG_VALUE);


        UUID uuid = UUID.randomUUID();
        this.gameState = new GameState(uuid, dealerId, activePlayerId, players, communityCards, pot);

        var eventData = EventData.builder()
            .value(PlayerAction.START_GAME.getDescription())
            .build();

        return GameEvent.builder()
            .gameId(game.getId())
            .playerId(playerId)
            .type(action.getType())
            .data(eventData)
            .createdAt(now)
            .build();
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }
}
