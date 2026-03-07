package poker.dto.game;

import lombok.Builder;
import poker.model.Player;

import java.util.List;

@Builder
public record GameStateDTO(GameDTO game, List<Player> players) {
}
