package poker.dto.player;

import lombok.Builder;

@Builder
public record PlayerDTO(
    long id,
    String nickname,
    int status,
    int chips,
    int currentBet) {
}
