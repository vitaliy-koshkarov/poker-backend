package poker.model.dto;

public record LoginRequest(
    String email,
    String password) {
}
