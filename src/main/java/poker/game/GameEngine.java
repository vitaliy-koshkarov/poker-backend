package poker.game;

public interface GameEngine {
    GameEvent handleAction(Long playerId, PlayerAction action);

    GameState getGameState();
}
