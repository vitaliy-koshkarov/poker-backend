package poker.dto.profile;

public record UpdatePasswordRequest(
    String currentPassword,
    String newPassword) {
}
