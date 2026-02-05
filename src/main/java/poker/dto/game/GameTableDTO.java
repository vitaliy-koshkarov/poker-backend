package poker.dto.game;

public record GameTableDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    String name) {
}
