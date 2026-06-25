package poker.game;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import poker.game.texasholdem.Card;

import java.util.List;

@Builder
@Getter
@ToString
public class GamePlayer {
    private long id;
    private String nickname;
    private int status;
    private int chips;
    private int currentBet;
    private List<Card> cards;
}
