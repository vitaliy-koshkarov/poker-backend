package poker.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum PlayerAction {
    STUB(0, "");

    private final int status;
    private final String description;
}
