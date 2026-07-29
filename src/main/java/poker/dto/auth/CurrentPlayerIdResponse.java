package poker.dto.auth;

import lombok.Builder;

@Builder
public record CurrentPlayerIdResponse(long currentPlayerId) {
}
