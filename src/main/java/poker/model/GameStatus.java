package poker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum GameStatus {
    WAITING_FOR_PLAYERS(0),
    PRE_FLOP(1),
    FLOP(2),
    TURN(3),
    RIVER(4),
    SHOWDOWN(5);

    private final int status;
}
