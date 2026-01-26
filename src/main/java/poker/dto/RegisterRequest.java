package poker.dto;

public record RegisterRequest(
    String email,
    String nickname,
    String password) {
}
