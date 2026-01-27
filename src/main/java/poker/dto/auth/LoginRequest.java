package poker.dto.auth;

public record LoginRequest(
    String email,
    String password) {
}
