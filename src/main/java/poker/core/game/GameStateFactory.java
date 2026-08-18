package poker.core.game;

import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;

import java.util.LinkedList;

public class GameStateFactory {

    public static GameState create(GameTable table) {
        return GameState.builder()
            .gameId(table.getId())
            .name(table.getName())
            .creatorPlayerId(table.getCreatorPlayerId())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .gameStatus(table.getGameStatus())
            .dealerId(table.getDealerId())
            .dealerIndex(table.getDealerIndex())
            .activePlayerId(table.getActivePlayerId())
            .smallBlind(table.getSmallBlind())
            .smallBlindPlayerId(table.getSmallBlindPlayerId())
            .bigBlind(table.getBigBlind())
            .bigBlindPlayerId(table.getBigBlindPlayerId())
            .minRaise(table.getMinRaise())
            .gamePot(table.getPot())
            .gamePlayers(new LinkedList<>(table.getPlayers()))
            .playersSeats(table.getPlayersSeats())
            .round(table.getRound())
            .deck(table.getDeck())
            .communityCards(new LinkedList<>(table.getCommunityCards()))
            .build();
    }

    public static GameState createSnapshot(GameTable table) {
        var snapshotGamePlayers = new LinkedList<GamePlayer>();
        for (GamePlayer gamePlayer : table.getPlayers()) {
            snapshotGamePlayers.add(gamePlayer.snapshot());
        }

        long[] snapshotPlayersSeats = new long[table.getMaxPlayers()];
        for (int i = 0; i < table.getPlayersSeats().length; i++) {
            snapshotPlayersSeats[i] = table.getPlayersSeats()[i];
        }

        Deck deckSnapshot = table.getDeck().snapshot();

        var snapshotCommunityCards = new LinkedList<Card>();
        for (Card card : table.getCommunityCards()) {
            snapshotCommunityCards.add(card.snapshot());
        }

        return GameState.builder()
            .gameId(table.getId())
            .name(table.getName())
            .creatorPlayerId(table.getCreatorPlayerId())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .gameStatus(table.getGameStatus())
            .dealerId(table.getDealerId())
            .dealerIndex(table.getDealerIndex())
            .activePlayerId(table.getActivePlayerId())
            .smallBlind(table.getSmallBlind())
            .smallBlindPlayerId(table.getSmallBlindPlayerId())
            .bigBlind(table.getBigBlind())
            .bigBlindPlayerId(table.getBigBlindPlayerId())
            .minRaise(table.getMinRaise())
            .gamePot(table.getPot().snapshot())
            .gamePlayers(snapshotGamePlayers)
            .playersSeats(snapshotPlayersSeats)
            .round(table.getRound().snapshot())
            .deck(deckSnapshot)
            .communityCards(snapshotCommunityCards)
            .build();
    }
}
