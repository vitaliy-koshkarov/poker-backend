package poker.dto.game;

import poker.model.Game;
import poker.model.Player;

import java.util.List;

public class GameConverter {
    public static GameDTO toDTO(Game game, List<Player> players, Integer currentPlayers) {
        return GameDTO.builder()
            .id(game.getId())
            .currentPlayers(currentPlayers)
            .maxPlayers(game.getMaxPlayers())
            .buyIn(game.getBuyIn())
            .status(game.getStatus())
            .name(game.getName())
            .players(players)
            .build();
    }
}
