package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.model.*;
import poker.repository.GameRepository;

import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class GameService {
    private final GameRepository gameRepo;
    private final PotService potService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;

    public GameService(GameRepository gameRepository, PotService potService,
                       PlayerService playerService, GameTableService gameTableService) {
        this.gameRepo = gameRepository;
        this.potService = potService;
        this.playerService = playerService;
        this.gameTableService = gameTableService;
    }

    public List<GameDTO> getGamesList() {
        List<GameDTO> gameDTOList = new LinkedList<>();
        List<Game> games = gameRepo.findAllGamesByOrderByIdAsc();

        List<GameTable> gameTables;
        for (Game game : games) {
            gameTables = gameTableService.getGameTablesByGameId(game.getId());
            GameDTO gameDTO = GameConverter.toDTO(game, gameTables.size());
            gameDTOList.add(gameDTO);
        }

        return gameDTOList;
    }

    public void createGame(CreateGameRequest createGameRequest) {
        var pot = potService.createPot();

        var game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .buyIn(createGameRequest.buyIn())
            .name(createGameRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS.getStatus())
            .potId(pot.getId())
            .build();

        var newGame = gameRepo.save(game);
        log.info("Game created {}", newGame);
    }

    public void removeGame(long gameId) {
        gameRepo.deleteById(gameId);
        log.info("Removed game id {}", gameId);
    }

    public Game getGameById(Long gameId) {
        return gameRepo.findGameById(gameId);
    }

    public Game updateGameName(Long gameId, String name) {
        gameRepo.updateGameName(gameId, name);
        log.info("Game name updated to {}", name);
        return gameRepo.findGameById(gameId);
    }

    public Game joinPlayerToGame(Long gameId, PlayerDetails playerDetails) {
//        Update player's status
        var player = playerDetails.getPlayer();
        player.setStatus(PlayerStatus.JOIN_THE_GAME.getStatus());
        long playerId = player.getId();
        playerService.updatePlayerStatus(playerId, PlayerStatus.JOIN_THE_GAME.getStatus());

//        Player sits down to game table
        var game = gameRepo.findGameById(gameId);
        Long userId = playerDetails.getUser().getId();
        var gameTable = gameTableService.createGameTable(userId, playerId, game.getId());

        log.info("User id {} joined game, game id {}, game table id {}", userId, game.getId(), gameTable.getId());

        return game;
    }
}
