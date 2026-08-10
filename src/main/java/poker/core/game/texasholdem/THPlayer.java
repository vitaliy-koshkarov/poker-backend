package poker.core.game.texasholdem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerStatus;
import poker.util.Util;

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
        currentBet = Util.ZERO_INT;
        status = PlayerStatus.WAIT;
    }

    @Override
    public void setChips(int chips) {
        this.chips = chips;
    }

    @Override
    public void bet(int bet) {
        if (chips - bet >= Util.ZERO_INT) {
            chips -= bet;
        } else {
            chips = Util.ZERO_INT;
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
