package poker.model.event;

import poker.core.engine.GameEngine;
import poker.core.game.card.Card;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.dto.CardConverter;
import poker.util.Util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEventFactory {

    public static GameEvent create(GameEngine engine, PlayerActionData pad) {
//        todo: create corresponding event according to action
        GameEventData gameEventData = GameEventData.builder()
            .gameId(engine.getTable().getId())
            .userId(pad.getPlayerDetails().getUser().getId())
            .playerId(pad.getPlayerDetails().getPlayer().getId())
            .potId(engine.getTable().getPot().getId())
            .dealerId(engine.getTable().getDealerId())
            .activePlayerId(engine.getTable().getActivePlayerId())
            .gameStatus(engine.getTable().getGameStatus().getIntStatus())
            .playerStatus(getPlayerStatus(
                engine.getTable().getPlayers(),
                pad.getPlayerDetails().getPlayer().getId())
            )
            .smallBlind(engine.getTable().getSmallBlind())
            .bigBlind(engine.getTable().getBigBlind())
            .buyIn(engine.getTable().getBuyIn())
            .actionType(pad.getPlayerAction().getType())
            .playersCards(toPlayersCards(engine.getTable().getPlayers()))
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

    private static int getPlayerStatus(List<GamePlayer> gamePlayers, long playerId) {
        for (GamePlayer gp : gamePlayers) {
            if (gp.getId() == playerId) {
                return gp.getStatus().getIntStatus();
            }
        }
        return Util.INVALID_INT_VALUE; // TODO: throw ex and handle it above
    }

    private static Map<Long, List<EventCard>> toPlayersCards(List<GamePlayer> gamePlayers) {
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
