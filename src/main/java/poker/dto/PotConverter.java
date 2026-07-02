package poker.dto;

import poker.core.game.GamePot;
import poker.core.player.GamePlayer;

import java.util.HashMap;
import java.util.Map;

public class PotConverter {

    public static PotDTO toDTO(GamePot gamePot) {
        var playersBets = new HashMap<Long, Integer>();
        for (Map.Entry<GamePlayer, Integer> pair : gamePot.getPlayersBets().entrySet()) {
            playersBets.put(pair.getKey().getId(), pair.getValue());
        }

        return PotDTO.builder()
            .total(gamePot.getTotal())
            .playersBets(playersBets)
            .build();
    }
}
