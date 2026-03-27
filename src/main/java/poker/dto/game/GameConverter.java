package poker.dto.game;

import poker.model.Game;

public class GameConverter {
    private final static int DEFAULT_INT_VALUE = 0;

    public static GameDTO toDTO(Game game, int currentPlayers) {
        return GameDTO.builder()
            .id(game.getId())
            .currentPlayers(currentPlayers)
            .maxPlayers(game.getMaxPlayers())
            .buyIn(game.getBuyIn())
            .status(game.getStatus())
            .name(game.getName())
            .creatorPlayerId(game.getCreatorPlayerId())
            .dealerId(game.getDealerId() != null ? game.getDealerId() : DEFAULT_INT_VALUE)
            .activePlayerId(game.getActivePlayerId() != null ? game.getActivePlayerId() : DEFAULT_INT_VALUE)
            .build();
    }
}
