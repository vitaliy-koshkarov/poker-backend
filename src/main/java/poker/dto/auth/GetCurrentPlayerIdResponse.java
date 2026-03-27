package poker.dto.auth;

import lombok.Builder;

@Builder
public record GetCurrentPlayerIdResponse(long currentPlayerId) {
}
