package poker.dto.table;

public record TableDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    String name) {
}
