package poker.dto.game;

public record CreateGameRequest(
    int maxPlayers,
    int buyIn,
    int smallBlind,
    int bigBlind,
    String name) {
}
