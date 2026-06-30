package poker.core.game;

import java.util.LinkedList;

public class GameStateFactory {
//    TODO: refactoring method parameter to interface
    public static GameState create(GameTable table) {
        return GameState.builder()
            .gameId(table.getId())
            .currentPlayers(table.getPlayers().size())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .status(table.getGameStatus().getIntStatus())
            .creatorPlayerId(table.getCreatorPlayerId())
            .dealerId(table.getDealerId())
            .name(table.getName())
            .smallBlind(table.getSmallBlind())
            .bigBlind(table.getBigBlind())
            .gamePlayerList(new LinkedList<>(table.getPlayers()))
            .build();
    }
}
