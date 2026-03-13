package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.game.GameEngine;
import poker.game.GameEvent;
import poker.game.GameState;
import poker.game.PlayerAction;
import poker.model.*;
import poker.repository.GameRepository;
import poker.game.GameRegistry;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class GameService {
    private final GameRegistry gameRegistry;
    private final GameRepository gameRepo;
    private final PotService potService;
    private final PlayerService playerService;
    private final GameTableService gameTableService;

    public GameService(GameRegistry gameRegistry, GameRepository gameRepository, PotService potService,
                       PlayerService playerService, GameTableService gameTableService) {
        this.gameRegistry = gameRegistry;
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
            .createdAt(new Timestamp(System.currentTimeMillis()))
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

    public void registerGame(Long gameId) {
        GameEngine gameEngine = gameRegistry.registerGame(gameId);
        log.info("Registered game id {}, game state {}", gameId, gameEngine.getGameState());
    }

    public GameState handleAction(Long gameId, Long playerId, PlayerAction action) {
        log.info("Handling action {} from player id {}", action, playerId);
        GameEngine engine = gameRegistry.getGameEngine(gameId);
        GameEvent gameEvent = engine.handleAction(playerId, action);
        UUID gameEventUuid = gameEvent.getEventUuid();
        log.info("Game event UUID {} player id {}", gameEventUuid, playerId);
//        TODO: save game event in DB
        GameState gameState = engine.getGameState();
        log.info("Game state after handling action {} from player id {}", gameState, playerId);
        return gameState;
    }
}
