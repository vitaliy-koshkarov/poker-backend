package poker.dto.game;

import lombok.Builder;
import poker.model.Player;

import java.util.List;

@Builder
public record GameDTO(
    long id,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    int status,
    String name,
    List<Player> players) {
}
