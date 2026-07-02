package poker.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record PotDTO(int total, Map<Long, Integer> playersBets) {
}
