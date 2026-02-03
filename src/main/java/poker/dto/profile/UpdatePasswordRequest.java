package poker.dto.profile;

public record UpdatePasswordRequest(
    String oldPassword,
    String newPassword) {
}
