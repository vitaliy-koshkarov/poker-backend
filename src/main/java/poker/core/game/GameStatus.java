package poker.core.game;

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
    SHOWDOWN(5),
    END(6);

    private final int intStatus;

    public static GameStatus getGameStatusByInt(int intStatus) {
        for (GameStatus gameStatus : values()) {
            if (gameStatus.getIntStatus() == intStatus) {
                return gameStatus;
            }
        }
        throw new EnumConstantNotPresentException(GameStatus.class, "There is no GameStatus with status " + intStatus);
    }
}
