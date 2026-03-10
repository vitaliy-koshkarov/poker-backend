package poker.game;

import lombok.Getter;

import java.util.UUID;

public class GameState {
    @Getter
    private final UUID uuidGameState;

    public GameState(UUID uuidGameState) {
        this.uuidGameState = uuidGameState;
    }

    @Override
    public String toString() {
        return "GameState{" + uuidGameState + "}";
    }
}
