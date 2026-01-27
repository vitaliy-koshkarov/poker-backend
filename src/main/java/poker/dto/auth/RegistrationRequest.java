package poker.dto.auth;

public record RegistrationRequest(
    String email,
    String nickname,
    String password) {
}
