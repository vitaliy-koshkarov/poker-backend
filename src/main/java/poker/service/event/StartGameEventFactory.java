package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.game.GameTable;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.dto.CardConverter;
import poker.model.event.EventCard;
import poker.model.event.GameEvent;
import poker.model.event.GameEventData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class StartGameEventFactory implements GameEventFactory {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.START_GAME;
    }

    @Override
    public GameEvent create(GameTable gameTable, PlayerActionData pad) {
        GameEventData gameEventData = GameEventData.builder()
            .gameId(gameTable.getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .potId(gameTable.getPot().getId())
            .dealerId(gameTable.getDealerId())
            .activePlayerId(gameTable.getActivePlayerId())
            .gameStatus(gameTable.getGameStatus().getIntStatus())
            .playerStatus(gameTable.getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .smallBlind(gameTable.getSmallBlind())
            .bigBlind(gameTable.getBigBlind())
            .buyIn(gameTable.getBuyIn())
            .actionType(pad.getPlayerAction().getType())
            .roundNumber(gameTable.getRound().getRoundNumber())
            .lastAggressorPlayerId(gameTable.getRound().getLastAggressorPlayerId())
            .lastMaxBet(gameTable.getRound().getLastMaxBet())
            .playersToAct(gameTable.getRound().getPlayersToAct())
            .playerIdsAndCards(toPlayerIdsAndCardsMap(gameTable.getPlayers()))
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(gameTable.getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .potId(gameTable.getPot().getId())
            .type(pad.getPlayerAction().getType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(pad.getDateTimeMs()))
            .build();
    }

    private static Map<Long, List<EventCard>> toPlayerIdsAndCardsMap(List<GamePlayer> gamePlayers) {
        var playersCards = new HashMap<Long, List<EventCard>>();
        for (GamePlayer gamePlayer : gamePlayers) {
            var cards = new ArrayList<EventCard>();
            for (Card card : gamePlayer.getCards()) {
                cards.add(CardConverter.toEventCard(card));
            }
            playersCards.put(gamePlayer.getId(), cards);
        }
        return playersCards;
    }
}
