package poker.dto.game;

import lombok.Builder;
import poker.dto.player.PlayerDTO;

import java.util.List;

@Builder
public record GameStateDTO(GameDTO gameDTO, List<PlayerDTO> playerDTOList) {
}
