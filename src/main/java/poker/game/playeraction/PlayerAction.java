package poker.game.playeraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlayerAction {
    JOIN_GAME(0, PlayerActions.JOIN_GAME),
    START_GAME(1, PlayerActions.START_GAME),
    DISCONNECT(2, PlayerActions.DISCONNECT),
    FOLD(3, PlayerActions.FOLD),
    CHECK(4, PlayerActions.CHECK),
    BET(5, PlayerActions.BET),
    ALL_IN(6, PlayerActions.ALL_INN);

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
