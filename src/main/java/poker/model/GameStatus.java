package poker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum GameStatus {
    WAITING_FOR_PLAYERS(0),
    START(1),
    PRE_FLOP(2),
    FLOP(3),
    TURN(4),
    RIVER(5),
    SHOWDOWN(6),
    END(7);

    private final int status;
}
