package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.model.Game;
import poker.repository.GameRepository;
import texasholdem.GameStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class GameService {
    private final GameRepository gameRepo;

    public GameService(GameRepository gameRepository) {
        this.gameRepo = gameRepository;
    }

    public List<GameDTO> getGamesList() {
        var gamesList = gameRepo.findAllGamesByOrderByIdAsc();

        var gamesDtoList = new LinkedList<GameDTO>();
        for (Game game : gamesList) {
            gamesDtoList.add(
                new GameDTO(game.getId(), 0, game.getMaxPlayers(), game.getBuyIn(), game.getName())
            );
        }

        return gamesDtoList;
    }

    public void addGame(CreateGameRequest createGameRequest) {
        Game game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .currentPlayers(Collections.emptySet())
            .buyIn(createGameRequest.buyIn())
            .name(createGameRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS)
            .build();

        Game savedGame = gameRepo.save(game);
        log.info("Game saved {}", savedGame);
    }

    public void removeGame(long id) throws Exception {
        var game = gameRepo.findById(id).orElseThrow(() -> new Exception("asd"));
        log.info("Removed game {}", game);
        gameRepo.deleteById(id);
    }
}
