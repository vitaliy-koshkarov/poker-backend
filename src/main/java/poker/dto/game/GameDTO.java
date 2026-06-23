package poker.dto.game;

import lombok.Builder;

@Builder
public record GameDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    int smallBlind,
    int bigBlind,
    int status,
    String name,
    long creatorPlayerId,
    long dealerId,
    long activePlayerId) {
}
