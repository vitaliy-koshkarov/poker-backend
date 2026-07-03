package poker.dto.game;

import lombok.Builder;
import poker.dto.CardDTO;
import poker.dto.PotDTO;
import poker.dto.player.PlayerDTO;

import java.util.List;

@Builder
public record GameDTO(
    long id,
    String name,
    long creatorPlayerId,
    int currentPlayers,
    int maxPlayers,
    int buyIn,
    String status,
    long dealerId,
    long activePlayerId,
    int smallBlind,
    int bigBlind,
    int minRaise,
    PotDTO pot,
    List<PlayerDTO> players,
    List<CardDTO> communityCards) {
}
