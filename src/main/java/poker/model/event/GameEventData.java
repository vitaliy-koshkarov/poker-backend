package poker.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import poker.core.player.GamePlayer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@ToString
public class GameEventData implements Serializable {
    private Long gameId;
    private Long userId;
    private Long playerId;
    private Long potId;
    private Long dealerId;
    private Long activePlayerId;
    private Integer seatNumber;
    private Integer gameStatus;
    private Integer playerStatus;
    private Integer smallBlind;
    private Integer bigBlind;
    private Integer buyIn;
    private Integer actionType;
    private Integer currentBet;
    private Integer roundNumber;
    private Long lastAggressorPlayerId;
    private Integer lastMaxBet;
    private List<Long> playersToAct;
    /**
     * Key - {@link GamePlayer#getId()}, value - list of {@link poker.core.game.card.Card}
     */
    private Map<Long, List<EventCard>> playerIdsAndCards;
    private List<EventCard> communityCards;
    private Long dateTimeMs;
}
