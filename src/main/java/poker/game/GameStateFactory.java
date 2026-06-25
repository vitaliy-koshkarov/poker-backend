package poker.game;

import poker.game.texasholdem.THTable;
import poker.util.Util;

import java.util.LinkedList;
import java.util.List;

public class GameStateFactory {
//    TODO: refactoring method parameter to interface
    public static GameState create(THTable table) {
//        TODO: refactoring THTable List<THPlayer> to List<GamePlayer>
        List<GamePlayer> gamePlayerList = new LinkedList<>();

        return GameState.builder()
            .gameId(table.getId())
            .currentPlayers(table.getPlayers().size())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .status(table.getGameStatus().getStatus())
            .creatorPlayerId(table.getCreatorPlayerId())
            .dealerId(table.getPlayers().isEmpty() ? Util.DEFAULT_LONG_VALUE : table.getPlayers().get(table.getDealerIdx()).getId())
            .name(table.getName())
            .smallBlind(table.getSmallBlind())
            .bigBlind(table.getBigBlind())
            .gamePlayerList(gamePlayerList)
            .build();
    }
}
