package poker.dto.game;

public record CreateGameRequest(
    int maxPlayers,
    int buyIn,
    String name) {
}
