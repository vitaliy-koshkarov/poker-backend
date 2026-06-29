package poker.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlayerAction {
    JOIN_GAME(0, "JOIN"),
    START_GAME(1, "START"),
    DISCONNECT(2, "DISCONNECT"),
    FOLD(3, "FOLD"),
    CHECK(4, "CHECK"),
    BET(5, "BET"),
    ALL_IN(6, "ALL_IN");

    private final int type;
    private final String actionName;

    public static PlayerAction fromActionName(String actionName) {
        for (PlayerAction playerAction : values()) {
            if (playerAction.getActionName().equals(actionName)) {
                return playerAction;
            }
        }
        throw new EnumConstantNotPresentException(PlayerAction.class, "Unknown action " + actionName);
    }
}
