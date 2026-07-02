package poker.dto.player;

import poker.core.player.GamePlayer;

import java.util.LinkedList;
import java.util.List;

public class PlayerConverter {

    public static List<PlayerDTO> toPlayerDTO(List<GamePlayer> gamePlayerList) {
        var playersDTOList = new LinkedList<PlayerDTO>();
        for (GamePlayer gamePlayer : gamePlayerList) {
            playersDTOList.add(
                PlayerDTO.builder()
                    .id(gamePlayer.getId())
                    .nickname(gamePlayer.getNickname())
                    .status(gamePlayer.getStatus().getIntStatus()) // todo: return String status (do not expose internal implementation details)
                    .chips(gamePlayer.getChips())
                    .currentBet(gamePlayer.getCurrentBet())
                    .build()
            );
        }
        return playersDTOList;
    }
}
