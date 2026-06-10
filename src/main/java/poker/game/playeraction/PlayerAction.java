package poker.game.playeraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlayerAction {
    JOIN_GAME(0, PlayerActions.JOIN_GAME, "Player join to the game"),
    START_GAME(1, PlayerActions.START_GAME, "Player started the game"),
    DISCONNECT(2, PlayerActions.DISCONNECT, "Player disconnected"),
    FOLD(3, PlayerActions.FOLD, "Player fold"),
    CHECK(4, PlayerActions.CHECK, "Player check"),
    BET(5, PlayerActions.BET, "Player bet"),
    ALL_IN(6, PlayerActions.ALL_INN, "Player all-in");

    private final int type;
    private final String actionName;
    private final String description;
}
