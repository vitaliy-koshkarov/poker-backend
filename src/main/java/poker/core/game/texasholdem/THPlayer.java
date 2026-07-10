package poker.core.game.texasholdem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerStatus;

import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
public class THPlayer implements GamePlayer {
    private final long id;
    private final String nickname;

    @Setter
    private PlayerStatus status;

    private int chips;

    @Setter
    private int currentBet;

    private final List<Card> cards;

    @Override
    public void refresh() {
        cards.clear();
        currentBet = 0;
        status = PlayerStatus.WAIT;
    }

    @Override
    public void setChips(int chips) {
        this.chips = chips;
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
    public GamePlayer snapshot() {
        return THPlayer.builder()
            .id(this.id)
            .nickname(this.nickname)
            .status(this.status)
            .chips(this.chips)
            .currentBet(this.currentBet)
            .cards(new LinkedList<>(cards))
            .build();
    }

    @Override
    public String toString() {
        return "Player{id: " + id + ", nickname: " + nickname + ", status: " + status
            + ", chips: " + chips + ", current bet: " + currentBet +
            ", Cards{" + cards + "}";
    }
}
