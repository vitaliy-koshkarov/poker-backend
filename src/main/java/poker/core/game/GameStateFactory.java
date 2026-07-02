package poker.core.game;

import poker.core.game.card.Card;
import poker.core.game.card.Deck;
import poker.core.player.GamePlayer;

import java.util.LinkedList;

public class GameStateFactory {

    public static GameState create(GameTable table) {
        return GameState.builder()
            .gameId(table.getId())
            .maxPlayers(table.getMaxPlayers())
            .buyIn(table.getBuyIn())
            .gameStatus(table.getGameStatus().getIntStatus())
            .creatorPlayerId(table.getCreatorPlayerId())
            .dealerId(table.getDealerId())
            .name(table.getName())
            .smallBlind(table.getSmallBlind())
            .bigBlind(table.getBigBlind())
            .gamePlayers(new LinkedList<>(table.getPlayers()))
            .gamePot(table.getPot())
            .build();
    }

    public static GameState createSnapshot(GameTable table) {
        var snapshotGamePlayers = new LinkedList<GamePlayer>();
        table.getPlayers().forEach(gamePlayer -> snapshotGamePlayers.add(gamePlayer.snapshot()));

        Deck deckSnapshot = table.getDeck().snapshot();

        var snapshotCommunityCards = new LinkedList<Card>();
        table.getCommunityCards().forEach(card -> snapshotCommunityCards.add(card.snapshot()));

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
