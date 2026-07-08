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
            .gameId(engine.getTable().getId())
            .userId(pad.getPlayerDetails().getUser().getId())
            .playerId(pad.getPlayerDetails().getPlayer().getId())
            .potId(engine.getTable().getPot().getId())
            .dealerId(engine.getTable().getDealerId())
            .activePlayerId(engine.getTable().getActivePlayerId())
            .gameStatus(engine.getTable().getGameStatus().getIntStatus())
            .playerStatus(EventUtil.getPlayerStatus(
                engine.getTable().getPlayers(),
                pad.getPlayerDetails().getPlayer().getId())
            )
            .smallBlind(engine.getTable().getSmallBlind())
            .bigBlind(engine.getTable().getBigBlind())
            .buyIn(engine.getTable().getBuyIn())
            .actionType(pad.getPlayerAction().getType())
            .playerIdsAndCards(toPlayerIdsAndCardsMap(engine.getTable().getPlayers()))
            .dateTimeMs(pad.getDateTimeMs())
            .build();

        return GameEvent.builder()
            .gameId(engine.getTable().getId())
            .userId(pad.getPlayerDetails().getUser().getId())
            .playerId(pad.getPlayerDetails().getPlayer().getId())
            .potId(engine.getTable().getPot().getId())
            .type(pad.getPlayerAction().getType())
            .gameEventData(gameEventData)
            .createdAt(new Timestamp(pad.getDateTimeMs()))
            .build();
    }

    private static Map<Long, List<EventCard>> toPlayerIdsAndCardsMap(List<GamePlayer> gamePlayers) {
        var playersCards = new HashMap<Long, List<EventCard>>();
        for (GamePlayer gp : gamePlayers) {
            var cards = new ArrayList<EventCard>();
            for (Card card : gp.getCards()) {
                cards.add(CardConverter.toEventCard(card));
            }
            playersCards.put(gp.getId(), cards);
        }
        return playersCards;
    }
}
