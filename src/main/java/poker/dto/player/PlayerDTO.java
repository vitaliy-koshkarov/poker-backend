package poker.dto.player;

import lombok.Builder;

@Builder
public record PlayerDTO(
    long id,
    String nickname,
    String status,
    int chips,
    int currentBet) {
}
