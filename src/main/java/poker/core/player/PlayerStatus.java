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
    NOT_IN_GAME(0, "Not in the game"),
    /**
     * Player join the game(table). He can not play at the moment, just watch the game
     */
    SPECTATOR(1, "Spectator"),
    /**
     * Player sat down at the table
     */
    JOIN_THE_GAME(2, "Joined"),
    /**
     * Player is waiting engine handling
     */
    WAIT(3, "Wait"),
    /**
     * Player discards the hand
     */
    FOLD(4, "Fold"),
    /**
     * Player did not bet, and pass the turn
     */
    CHECK(5, "Check"),
    /**
     * Player matched to the highest bet
     */
    CALL(6, "Call"),
    /**
     * Player made a bet
     */
    BET(7, "Bet"),
    /**
     * Player increased the last highest bet
     */
    RAISE(8, "Raise"),
    /**
     * Player bet all chips
     */
    ALL_IN(9, "All-in"),
    /**
     * Player disconnected
     */
    DISCONNECT(10, "Disconnect"),
    /**
     * Player's turn
     */
    ACTIVE(11, "Active");

    private final int intStatus;
    private final String shortName;
}
