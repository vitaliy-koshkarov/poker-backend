package poker.dto.game;

import lombok.Builder;

@Builder
public record GameTableDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    String name) {
}
