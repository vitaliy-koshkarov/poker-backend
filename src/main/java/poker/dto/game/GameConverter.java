package poker.dto.game;

import poker.model.Game;

public class GameConverter {
    public static GameDTO toDTO(Game game, Integer currentPlayers) {
        return GameDTO.builder()
            .id(game.getId())
            .currentPlayers(currentPlayers)
            .maxPlayers(game.getMaxPlayers())
            .buyIn(game.getBuyIn())
            .name(game.getName())
            .build();
    }
}
