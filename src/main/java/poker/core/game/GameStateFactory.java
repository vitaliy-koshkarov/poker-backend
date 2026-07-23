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
            .activePlayerId(table.getActivePlayerId())
            .smallBlind(table.getSmallBlind())
            .bigBlind(table.getBigBlind())
            .minRaise(table.getMinRaise())
            .gamePot(table.getPot())
            .gamePlayers(new LinkedList<>(table.getPlayers()))
            .communityCards(new LinkedList<>(table.getCommunityCards()))
            .build();
    }

    public static GameState createSnapshot(GameTable table) {
        var snapshotGamePlayers = new LinkedList<GamePlayer>();
        for (GamePlayer gamePlayer : table.getPlayers()) {
            snapshotGamePlayers.add(gamePlayer.snapshot());
        }

        Deck deckSnapshot = table.getDeck().snapshot();

        var snapshotCommunityCards = new LinkedList<Card>();
        for (Card card : table.getCommunityCards()) {
            snapshotCommunityCards.add(card.snapshot());
        }

        long[] snapshotPlayersSeats = new long[table.getMaxPlayers()];
        for (int i = 0; i < table.getPlayersSeats().length; i++) {
            snapshotPlayersSeats[i] = table.getPlayersSeats()[i];
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
            .deck(deckSnapshot)
            .communityCards(snapshotCommunityCards)
            .playersSeats(snapshotPlayersSeats)
            .build();
    }
}
