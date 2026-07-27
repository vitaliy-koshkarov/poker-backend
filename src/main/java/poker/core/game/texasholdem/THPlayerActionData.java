package poker.core.game.texasholdem;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

@Builder
@Getter
@ToString
public class THPlayerActionData implements PlayerActionData {
    private final long gameId;
    private final PlayerAction playerAction;
    private final long userId;
    private final long playerId;
    private final String nickname;
    private final int chips;
    private final int playerBet;
    private final long dateTimeMs;
}
