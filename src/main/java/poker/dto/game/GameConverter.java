package poker.dto.game;

import poker.model.Game;
import poker.model.GameTable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameConverter {
    public static GameDTO toDTO(Game game, Integer currentPlayers) {
        return GameDTO.builder()
            .id(game.getId())
            .currentPlayers(currentPlayers)
            .maxPlayers(game.getMaxPlayers())
            .buyIn(game.getBuyIn())
            .name(game.getName())
            .build();
    }

    public static List<GameDTO> toDTOList(List<Game> games, List<GameTable> gameTables) {
        List<GameDTO> result = new LinkedList<>();

//        TODO: N^2 not good, try improve
        for (Game game : games) {
            GameDTO.GameDTOBuilder builder = GameDTO.builder()
                .id(game.getId())
                .maxPlayers(game.getMaxPlayers())
                .buyIn(game.getBuyIn())
                .name(game.getName());

            int currentPlayers = 0;
            for (GameTable gameTable : gameTables) {
                if (Objects.equals(gameTable.getGameId(), game.getId())) {
                    currentPlayers++;
                }
            }

            GameDTO gameDTO = builder.currentPlayers(currentPlayers).build();
            result.add(gameDTO);
        }

        return result;
    }
}
