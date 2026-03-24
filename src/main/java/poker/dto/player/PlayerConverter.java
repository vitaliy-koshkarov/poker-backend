package poker.dto.player;

import poker.model.Player;

import java.util.LinkedList;
import java.util.List;

public class PlayerConverter {

    public static LinkedList<PlayerDTO> toDTO(List<Player> players) {
        var playerDTOList = new LinkedList<PlayerDTO>();
        for (Player player : players) {
            var playerDTO = PlayerDTO.builder()
                .id(player.getId())
                .nickname(player.getNickname())
                .status(player.getStatus())
                .chips(player.getChips())
                .currentBet(player.getCurrentBet())
                .build();
            playerDTOList.add(playerDTO);
        }

        return playerDTOList;
    }
}
