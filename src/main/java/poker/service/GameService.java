package poker.service;

import lombok.ToString;
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

@Service("GameService")
@Log4j2
@ToString
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
        List<Game> games = gameRepo.findAllNotEndedGames(GameStatus.END.getStatus());

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

        gameTableService.deleteGameTableByIdGameId(gameId);
        log.info("Removed game table with game id {}", gameId);

        gameRepo.deleteById(gameId);
        log.info("Removed game id {}", gameId);

        potService.deleteById(potId);
        log.info("Removed pot id {}", potId);
    }

    public Game getGameById(Long gameId) {
        return gameRepo.findGameById(gameId);
    }

    /**
     * Updates player's status to {@link PlayerStatus#JOIN_THE_GAME} and chips.</br>
     * Created {@link GameTable} entity
     * @param gameId {@link Game#getId()}
     * @param playerDetails {@link PlayerDetails} with info of this {@link User} and {@link Player}
     */
    public void joinPlayerToGame(long gameId, PlayerDetails playerDetails) {
        var game = gameRepo.findGameById(gameId);
        var player = playerDetails.getPlayer();
        long userId = playerDetails.getUser().getId();

//        TODO: think how to handle accidental disconnects
        var gameTable = gameTableService.getGameTableByGameIdAndPlayerId(gameId, player.getId());
        if (gameTable != null) {
            log.info("PLAYER {} CHIPS {}", player.getId(), player.getChips());
            player.setChips(player.getChips());
        } else {
            player.setChips(game.getBuyIn());

            gameTable = gameTableService.createGameTable(userId, player.getId(), game.getId());
        }
        log.info("PLAYER {} JOIN, GAME TABLE {}", player.getId(), gameTable);

        player.setStatus(PlayerStatus.JOIN_THE_GAME.getStatus());

        playerService.updatePlayer(player);

        log.info("User id {} joined, game id {}, game table id {}", userId, game.getId(), gameTable.getId());
    }

    public void updateGame(Game game) {
        gameRepo.save(game);
        log.info("Updated game {}", game);
    }
}
