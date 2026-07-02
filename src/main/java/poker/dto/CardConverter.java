package poker.dto;

import poker.core.game.card.Card;

import java.util.LinkedList;
import java.util.List;

public class CardConverter {

    public static List<CardDTO> toCardDTOList(List<Card> cards) {
        var cardDTOList = new LinkedList<CardDTO>();
        for (Card card : cards) {
            cardDTOList.add(
                CardDTO.builder()
                    .rank(card.rank().getShortName())
                    .suit(card.suit().getShortName())
                    .build()
            );
        }
        return cardDTOList;
    }
}
