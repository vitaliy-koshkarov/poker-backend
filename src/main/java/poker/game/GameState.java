package poker.game;

import poker.model.Player;
import poker.model.Pot;

import java.util.List;
import java.util.UUID;

public record GameState(UUID uuidGameState,
                        Long dealerId,
                        Long activePlayerId,
                        List<Player> players,
                        Pot pot) {
}
