package poker.dto.game;

import poker.dto.player.PlayerDTO;
import poker.core.game.GameState;

import java.util.LinkedList;
import java.util.List;

public class GameStateConverter {
    public static GameStateDTO toDTO(GameState gameState) {
//        TODO: refactoring
        GameDTO gameDTO = GameDTO.builder()
            .id(gameState.getGameId())
            .currentPlayers(gameState.getCurrentPlayers())
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
        gameState.getGamePlayerList().forEach(gamePlayer -> playerDTOList.add(PlayerDTO.builder()
            .id(gamePlayer.getId())
            .nickname(gamePlayer.getNickname())
            .status(gamePlayer.getStatus().getIntStatus())
            .chips(gamePlayer.getChips())
            .currentBet(gamePlayer.getCurrentBet())
            .build()));

        return GameStateDTO.builder()
            .gameDTO(gameDTO)
            .playerDTOList(playerDTOList)
            .build();
    }
}
