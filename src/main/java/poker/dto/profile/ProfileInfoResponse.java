package poker.dto.profile;

import lombok.Builder;

@Builder
public record ProfileInfoResponse(
    String email,
    String nickname) {
}
