package poker.core.game.texasholdem;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.model.PlayerDetails;

@Builder
@Getter
@ToString
public class THPlayerActionData implements PlayerActionData {
    private final long gameId;
    private final PlayerAction playerAction;
    private final PlayerDetails playerDetails;
}
