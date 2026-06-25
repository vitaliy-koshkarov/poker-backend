package poker.game;

public interface GameEngine {
    GameState getCurrentGameState();

    void handlePlayerAction(long playerId, PlayerActionData playerActionData, PlayerAction playerAction);
}
