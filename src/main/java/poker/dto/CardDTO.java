package poker.dto;

import lombok.Builder;

@Builder
public record CardDTO(String rank, String suit) {
}
