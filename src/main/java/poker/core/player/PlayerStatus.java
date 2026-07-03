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
    NOT_IN_GAME(0, "Not_in_game"),
    /**
     * Player join the game(table). He can not play at the moment, just watch the game
     */
    JOIN_THE_GAME(1, "Joined"),
    /**
     * Player is waiting actions from other players
     */
    WAIT(2, "Wait"),
    /**
     * Player discards the hand
     */
    FOLD(3, "Fold"),
    /**
     * Player's turn
     */
    ACTIVE(4, "Active");

    private final int intStatus;
    private final String shortName;
}
