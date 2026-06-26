package poker.game.texasholdem;

import lombok.Getter;
import lombok.Setter;
import poker.game.GamePlayer;
import poker.game.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class THPlayer implements GamePlayer {
    private final long id;
    private final String nickname;

    @Setter
    private PlayerStatus status;

    private int chips;
    private int currentBet;
    private final List<Card> cards;

    public THPlayer(long id, String name, int chips) {
        this.id = id;
        this.nickname = name;
        this.status = PlayerStatus.NOT_IN_GAME;
        this.chips = chips;
        this.cards = new ArrayList<>();
    }

    @Override
    public void refresh() {
        cards.clear();
        currentBet = 0;
        status = PlayerStatus.WAIT;
    }

    @Override
    public void bet(int bet) {
        if (chips - bet >= 0) {
            chips -= bet;
        } else {
            chips = 0;
        }
        currentBet += bet;
    }

    @Override
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
