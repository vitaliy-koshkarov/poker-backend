package poker.dto;

import poker.core.game.GamePot;

import java.util.HashMap;
import java.util.Map;

public class PotConverter {

    public static PotDTO toDTO(GamePot gamePot) {
        Map<Long, Integer> playersBets = new HashMap<>(gamePot.getPlayersBets());

        return PotDTO.builder()
            .total(gamePot.getTotal())
            .playersBets(playersBets)
            .build();
    }
}
