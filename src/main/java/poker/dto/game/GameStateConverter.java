package poker.dto.game;

import poker.dto.CardConverter;
import poker.dto.PotConverter;
import poker.core.game.GameState;
import poker.dto.player.PlayerConverter;

public class GameStateConverter {

    public static GameDTO toGameStateDTOInLobby(GameState gameState) {
        return GameDTO.builder()
            .id(gameState.getGameId())
            .name(gameState.getName())
            .creatorPlayerId(gameState.getCreatorPlayerId())
            .currentPlayers(gameState.getGamePlayers().size())
            .maxPlayers(gameState.getMaxPlayers())
            .buyIn(gameState.getBuyIn())
            .status(gameState.getGameStatus()) // TODO: return String status (do not expose internal implementation details)
            .build();
    }

    public static GameDTO toGameFlowGameStateDTO(GameState gameState) {
        return GameDTO.builder()
            .id(gameState.getGameId())
            .name(gameState.getName())
            .creatorPlayerId(gameState.getCreatorPlayerId())
            .currentPlayers(gameState.getGamePlayers().size())
            .maxPlayers(gameState.getMaxPlayers())
            .buyIn(gameState.getBuyIn())
            .status(gameState.getGameStatus()) // TODO: return String status (do not expose internal implementation details)
            .dealerId(gameState.getDealerId())
            .activePlayerId(gameState.getActivePlayerId())
            .smallBlind(gameState.getSmallBlind())
            .bigBlind(gameState.getBigBlind())
            .minRaise(gameState.getMinRaise())
            .pot(PotConverter.toDTO(gameState.getGamePot()))
            .players(PlayerConverter.toPlayerDTO(gameState.getGamePlayers()))
            .communityCards(CardConverter.toCardDTOList(gameState.getCommunityCards()))
            .build();
    }
}
