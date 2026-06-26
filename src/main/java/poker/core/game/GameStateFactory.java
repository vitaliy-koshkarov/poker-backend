package poker.core.game;

import poker.core.player.GamePlayer;

import java.util.LinkedList;
import java.util.List;

public class GameStateFactory {
//    TODO: refactoring method parameter to interface
    public static GameState create(GameTable table) {
//        TODO: refactoring THTable List<THPlayer> to List<GamePlayer>
        List<GamePlayer> gamePlayerList = new LinkedList<>();

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
            .gamePlayerList(gamePlayerList)
            .build();
    }
}
