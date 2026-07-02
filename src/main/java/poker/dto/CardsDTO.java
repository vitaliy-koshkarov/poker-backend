package poker.dto;

import lombok.Builder;
import poker.core.game.card.Card;

import java.util.List;

@Builder
public record CardsDTO(List<Card> cards) {
}
