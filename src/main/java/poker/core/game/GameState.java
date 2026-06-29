package poker.core.game;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import poker.core.player.GamePlayer;

import java.util.List;

@Builder
@Getter
@ToString
public class GameState {
    private long gameId;
    private int currentPlayers;
    private int maxPlayers;
    private int buyIn;
    private int status;
    private long creatorPlayerId;
    private long dealerId;
    private long activePlayerId;
    private String name;
    private int smallBlind;
    private int bigBlind;

    private List<GamePlayer> gamePlayerList;
}
