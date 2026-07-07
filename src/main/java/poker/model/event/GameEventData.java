package poker.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class GameEventData implements Serializable {
    private long gameId;
    private long userId;
    private long playerId;
    private long potId;
    private long gameSeatId;
    private long dealerId;
    private long activePlayerId;
    private int seatNumber;
    private int gameStatus;
    private int playerStatus;
    private int smallBlind;
    private int bigBlind;
    private int buyIn;
    private int actionType;
    private int playerChips;
    private int currentBet;
    private int potTotal;
    private List<EventCard> playerCards;
    private List<EventCard> communityCards;
    private long dateTimeMs;
}
