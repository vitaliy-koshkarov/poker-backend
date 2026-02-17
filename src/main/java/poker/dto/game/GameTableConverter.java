package poker.dto.game;

import poker.model.GameTable;

public class GameTableConverter {
    public static GameTableDTO toDTO(GameTable gameTable) {
        return GameTableDTO.builder()
            .id(gameTable.getId())
            .currentPlayers(gameTable.getCurrentPlayers().size())
            .maxPlayers(gameTable.getMaxPlayers())
            .buyIn(gameTable.getBuyIn())
            .name(gameTable.getName())
            .build();
    }
}
