package poker.dto.game;

import lombok.Builder;

@Builder
public record GameDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    int status,
    String name) {
}
