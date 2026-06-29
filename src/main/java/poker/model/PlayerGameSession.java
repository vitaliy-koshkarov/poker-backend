package poker.model;

//TODO: store player sessions somewhere and restore them after app reboot
public record PlayerGameSession(long userId, long playerId, long gameId) {
}
