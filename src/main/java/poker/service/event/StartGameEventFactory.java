package poker.service.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.core.engine.GameEngine;
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
    public GameEvent create(GameEngine engine, PlayerActionData pad) {
        GameEventData gameEventData = GameEventData.builder()
            .gameId(engine.table().getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .potId(engine.table().getPot().getId())
            .dealerId(engine.table().getDealerId())
            .activePlayerId(engine.table().getActivePlayerId())
            .gameStatus(engine.table().getGameStatus().getIntStatus())
            .playerStatus(engine.table().getPlayerById(pad.getPlayerId()).getStatus().getIntStatus())
            .smallBlind(engine.table().getSmallBlind())
            .bigBlind(engine.table().getBigBlind())
            .buyIn(engine.table().getBuyIn())
            .actionType(pad.getPlayerAction().getType())
            .roundNumber(engine.table().getRound().getRoundNumber())
            .lastAggressorPlayerId(engine.table().getRound().getLastAggressorPlayerId())
            .lastMaxBet(engine.table().getRound().getLastMaxBet())
            .playersToAct(engine.table().getRound().getPlayersToAct())
            .playerIdsAndCards(toPlayerIdsAndCardsMap(engine.table().getPlayers()))
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(engine.table().getId())
            .userId(pad.getUserId())
            .playerId(pad.getPlayerId())
            .potId(engine.table().getPot().getId())
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
