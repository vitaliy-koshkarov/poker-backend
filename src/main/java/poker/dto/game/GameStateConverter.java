package poker.dto.game;

import poker.core.player.GamePlayer;
import poker.dto.CardsDTO;
import poker.dto.PotDTO;
import poker.dto.player.PlayerDTO;
import poker.core.game.GameState;

import java.util.LinkedList;
import java.util.List;

public class GameStateConverter {

    public static GameStateDTO toGameStateDTOInLobby(GameState gameState) {
        GameDTO gameDTO = GameDTO.builder()
            .id(gameState.getGameId())
            .name(gameState.getName())
            .currentPlayers(gameState.getGamePlayers().size())
            .maxPlayers(gameState.getMaxPlayers())
            .buyIn(gameState.getBuyIn())
            .status(gameState.getGameStatus()) // TODO: return String status (do not expose internal implementation details)
            .build();

        return new GameStateDTO(gameDTO);
    }

    public static GameStateDTO toDTO(GameState gameState) {
//        TODO: refactoring
        GameDTO gameDTO = GameDTO.builder()
            .id(gameState.getGameId())
            .name(gameState.getName())
            .creatorPlayerId(gameState.getCreatorPlayerId())
            .maxPlayers(gameState.getMaxPlayers())
            .buyIn(gameState.getBuyIn())
            .status(gameState.getGameStatus())
            .dealerId(gameState.getDealerId())
            .activePlayerId(gameState.getActivePlayerId())
            .smallBlind(gameState.getSmallBlind())
            .bigBlind(gameState.getBigBlind())
            .minRaise(gameState.getMinRaise())
            .pot(
                PotDTO.builder()
                    .total(gameState.getGamePot().getTotal())
                    .playersBets(gameState.getGamePot().getPlayersBets())
                    .build()
            )
            .communityCards(
                CardsDTO.builder()
                    .cards(gameState.getCommunityCards())
                    .build()
            )
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

        return new GameStateDTO(gameDTO);
//            .gameDTO(gameDTO)
//            .playerDTOList(playerDTOList)
//            .build();
    }
}
