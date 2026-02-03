package poker.dto.table;

public record CreateTableRequest(
    int maxPlayers,
    int buyIn,
    String name) {
}
