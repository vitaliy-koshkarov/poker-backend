package poker.model;

//TODO: store player sessions somewhere and restore them after app reboot
public record PlayerSession(Long userId, Long playerId, Long gameId) {
}
