package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameDTO;
import poker.model.Game;
import poker.repository.GameRepository;
import texasholdem.GameState;

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

        log.info("GamesList: {}", gamesList);

        var gamesDtoList = new LinkedList<GameDTO>();
        for (Game game : gamesList) {
            gamesDtoList.add(new GameDTO(game.getId(), 0, game.getMaxPlayers(),
                game.getBuyIn(), game.getName())
            );
        }

        log.info("Games: {}", gamesDtoList);

        return gamesDtoList;
    }

    public Game addGame(CreateGameRequest createGameRequest) {
        Game game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .currentPlayers(0)
            .buyIn(createGameRequest.buyIn())
            .name(createGameRequest.name())
            .status(GameState.WAITING_FOR_PLAYERS)
            .build();

        Game savedGame = gameRepo.save(game);
        log.info("Game saved {}", savedGame);

        return savedGame;
    }

    public void removeGame(long id) throws Exception {
        var game = gameRepo.findById(id).orElseThrow(() -> new Exception("asd"));
        log.info("Removed game {}", game);
        gameRepo.deleteById(id);
    }
}
