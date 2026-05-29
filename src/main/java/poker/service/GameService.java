package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.model.*;
import poker.repository.GameRepository;
import poker.util.Util;

import java.sql.Timestamp;
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

    public Game createGame(Long creatorPlayerId, CreateGameRequest createGameRequest) {
        var pot = potService.createPot();

        var game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .buyIn(createGameRequest.buyIn())
            .name(createGameRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS.getStatus())
            .potId(pot.getId())
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .creatorPlayerId(creatorPlayerId)
            .dealerId(Util.DEFAULT_LONG_VALUE)
            .activePlayerId(Util.DEFAULT_LONG_VALUE)
            .build();

        var newGame = gameRepo.save(game);
        log.info("Game created {} by player id {}", newGame, creatorPlayerId);

        return newGame;
    }

    public void removeGame(Long gameId) {
        var game = gameRepo.findGameById(gameId);
        Long potId = game.getPotId();

        gameRepo.deleteById(gameId);
        log.info("Removed game id {}", gameId);

        potService.deleteById(potId);
        log.info("Removed pot id {}", potId);
    }

    public Game getGameById(Long gameId) {
        return gameRepo.findGameById(gameId);
    }

    public Game joinPlayerToGame(Long gameId, PlayerDetails playerDetails) {
//        Update player's status
        var player = playerDetails.getPlayer();
        player.setStatus(PlayerStatus.JOIN_THE_GAME.getStatus());
//        TODO: check game state
        playerService.updatePlayer(player);

//        Player sits down to game table
        var game = gameRepo.findGameById(gameId);
        Long userId = playerDetails.getUser().getId();
        var gameTable = gameTableService.createGameTable(userId, player.getId(), game.getId());

        log.info("User id {} joined, game id {}, game table id {}", userId, game.getId(), gameTable.getId());

        return game;
    }

    public void updateGame(Game game) {
        gameRepo.save(game);
        log.info("Updated game {}", game);
    }
}
