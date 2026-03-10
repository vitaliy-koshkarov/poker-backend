package poker.game;

import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class GameEngineStub implements GameEngine {
    private GameState gameState;

    @Override
    public GameEvent handleAction(Long playerId, PlayerAction action) {
        log.info("Handle action {} player id {}", action, playerId);
        UUID uuid = UUID.randomUUID();
        this.gameState = new GameState(uuid);
        return new GameEventStub(uuid);
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }
}
