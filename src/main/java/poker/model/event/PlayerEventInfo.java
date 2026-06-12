package poker.model.event;

import lombok.*;
import poker.game.texasholdem.Card;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class PlayerEventInfo implements Serializable {
    private final long playerId;
    private final int playerStatus;
    private final int bet;
    private final List<Card> cards;
}
