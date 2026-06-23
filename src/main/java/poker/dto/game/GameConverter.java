package poker.dto.game;

import poker.model.Game;
import poker.util.Util;

public class GameConverter {

    public static GameDTO toDTO(Game game, int currentPlayers) {
        return GameDTO.builder()
            .id(game.getId())
            .currentPlayers(currentPlayers)
            .maxPlayers(game.getMaxPlayers())
            .buyIn(game.getBuyIn())
            .smallBlind(game.getSmallBlind())
            .bigBlind(game.getBigBlind())
            .status(game.getStatus())
            .name(game.getName())
            .creatorPlayerId(game.getCreatorPlayerId())
            .dealerId(game.getDealerId() != null ? game.getDealerId() : Util.DEFAULT_LONG_VALUE)
            .activePlayerId(game.getActivePlayerId() != null ? game.getActivePlayerId() : Util.DEFAULT_LONG_VALUE)
            .build();
    }
}
