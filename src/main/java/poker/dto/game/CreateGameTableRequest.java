package poker.dto.game;

public record CreateGameTableRequest(
    int maxPlayers,
    int buyIn,
    String name) {
}
