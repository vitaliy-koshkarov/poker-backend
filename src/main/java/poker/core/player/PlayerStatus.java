package poker.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlayerStatus {
    /**
     * The player is not in the game
     */
    NOT_IN_GAME(0),
    /**
     * Player join the game(table). He can not play at the moment, just watch the game
     */
    JOIN_THE_GAME(1),
    /**
     * Player is waiting actions from other players
     */
    WAIT(2),
    /**
     * Player discards the hand
     */
    FOLD(3),
    /**
     * Player's turn
     */
    ACTIVE(4);

    private final int intStatus;
}
