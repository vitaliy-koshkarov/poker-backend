package poker.dto.player;

import lombok.Builder;

@Builder
public record PlayerDTO(Long id, String nickname, Integer status, Integer chips, Integer currentBet) {
}
