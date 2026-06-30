package poker.dto.game;

import poker.core.player.GamePlayer;
import poker.dto.player.PlayerDTO;
import poker.core.game.GameState;

import java.util.LinkedList;
import java.util.List;

public class GameStateConverter {
    public static GameStateDTO toDTO(GameState gameState) {
//        TODO: refactoring
        GameDTO gameDTO = GameDTO.builder()
            .id(gameState.getGameId())
            .currentPlayers(gameState.getGamePlayers().size())
            .maxPlayers(gameState.getMaxPlayers())
            .buyIn(gameState.getBuyIn())
            .smallBlind(gameState.getSmallBlind())
            .bigBlind(gameState.getBigBlind())
            .status(gameState.getStatus())
            .name(gameState.getName())
            .creatorPlayerId(gameState.getCreatorPlayerId())
            .dealerId(gameState.getDealerId())
            .activePlayerId(gameState.getActivePlayerId())
            .build();

        List<PlayerDTO> playerDTOList = new LinkedList<>();
        for (GamePlayer gamePlayer : gameState.getGamePlayers()) {
            playerDTOList.add(
                PlayerDTO.builder()
                    .id(gamePlayer.getId())
                    .nickname(gamePlayer.getNickname())
                    .status(gamePlayer.getStatus().getIntStatus())
                    .chips(gamePlayer.getChips())
                    .currentBet(gamePlayer.getCurrentBet())
                    .build()
            );
        }

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
