package poker.core.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum GameStatus {
    WAITING_FOR_PLAYERS(0, "Waiting for players"),
    PRE_FLOP(1, "Pre-flop"),
    FLOP(2, "Flop"),
    TURN(3, "Turn"),
    RIVER(4, "River"),
    SHOWDOWN(5, "Showdown"),
    END(6, "End");

    private final int intStatus;
    private final String shortName;

    public static GameStatus getGameStatusByInt(int intStatus) {
        for (GameStatus gameStatus : values()) {
            if (gameStatus.getIntStatus() == intStatus) {
                return gameStatus;
            }
        }
        throw new EnumConstantNotPresentException(GameStatus.class, "There is no GameStatus with status " + intStatus);
    }
}
