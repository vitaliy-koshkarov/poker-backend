package poker.model;

//TODO: store player sessions somewhere and restore them after app reboot
public record PlayerGameSession(Long userId, Long playerId, Long gameId) {
}
