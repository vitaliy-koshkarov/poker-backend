package poker.dto.game;

public record GameDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    String name) {
}
