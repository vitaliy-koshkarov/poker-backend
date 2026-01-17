package poker.model.dto;

public record RegisterRequest(
    String email,
    String nickname,
    String password) {
}
