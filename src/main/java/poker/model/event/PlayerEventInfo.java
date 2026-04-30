package poker.model.event;

import lombok.*;
import poker.game.texasholdem.Card;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class PlayerEventInfo {
    private final long id;
    private final int playerStatus;
    private final int bet;
    private final List<Card> cards;
}
