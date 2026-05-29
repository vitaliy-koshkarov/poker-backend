package poker.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import poker.model.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class THPlayer {
    private final long id;
    private final String nickname;

    @Setter
    private PlayerStatus status;
    private final List<Card> cards;
    private int chips;
    private int currentBet;

    public THPlayer(long id, String name, int chips) {
        this.id = id;
        this.nickname = name;
        this.chips = chips;
        this.status = PlayerStatus.WAIT;
        this.cards = new ArrayList<>();
    }

    public void bet(int bet) {
        if (chips - bet >= 0) {
            chips -= bet;
        } else {
            chips = 0;
        }
        currentBet += bet;
    }

    public void refresh() {
        cards.clear();
        currentBet = 0;
        status = PlayerStatus.WAIT;
    }

    public void takeReward(int reward) {
        chips += reward;
    }

    @Override
    public String toString() {
        return "Player{id: " + id + ", nickname: " + nickname + ", status: " + status
            + ", chips: " + chips + ", current bet: " + currentBet +
            ", Cards{" + cards + "}";
    }
}
