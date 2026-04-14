package poker.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlayerAction {
    JOIN_GAME(0, "Player join to the game"),
    START_GAME(1, "Player started the game"),
    DISCONNECT(2, "Player disconnected"),
    FOLD(3, "Player fold"),
    CHECK(4, "Player check"),
    BET(5, "Player bet"),
    ALL_IN(6, "Player all-in");

    private final int type;
    private final String description;
}
