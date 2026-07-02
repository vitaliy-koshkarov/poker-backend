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
            .gameStatus(table.getGameStatus().getIntStatus())
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

        return GameState.builder()
            .gameId(table.getId())
            .name(table.getName())
            .creatorPlayerId(table.getCreatorPlayerId())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .gameStatus(table.getGameStatus().getIntStatus())
            .dealerId(table.getDealerId())
            .dealerIndex(table.getDealerIndex())
            .activePlayerId(table.getActivePlayerId())
            .activePlayerIndex(table.getActivePlayerIndex())
            .smallBlind(table.getSmallBlind())
            .smallBlindIndex(table.getSmallBlindIndex())
            .bigBlind(table.getBigBlind())
            .bigBlindIndex(table.getBigBlindIndex())
            .minRaise(table.getMinRaise())
            .gamePot(table.getPot().snapshot())
            .gamePlayers(snapshotGamePlayers)
            .deck(deckSnapshot)
            .communityCards(snapshotCommunityCards)
            .build();
    }
}
