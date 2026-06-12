package poker.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import poker.game.texasholdem.Card;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class GameEventData implements Serializable {
    private List<Card> communityCards;

    private List<PlayerEventInfo> playerEventInfo;
}
