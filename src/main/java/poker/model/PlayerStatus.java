package poker.model;

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
     * Player sat down at the table and can play
     */
    IN_GAME(2),
    /**
     * Player is waiting actions from other players
     */
    WAIT(3),
    /**
     * Player discards the hand
     */
    FOLD(4),
    /**
     * Player's turn
     */
    ACTIVE(5);

    private final int status;
}
