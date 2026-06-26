package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poker.config.GameProps;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.GameConverter;
import poker.dto.game.GameDTO;
import poker.game.PlayerStatus;
import poker.model.*;
import poker.repository.GameRepository;
import poker.util.Util;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Service("GameService")
@Log4j2
@RequiredArgsConstructor
@ToString
public class GameService {
    private final GameRepository gameRepo;
    private final PotService potService;
    private final PlayerService playerService;
    private final GameSeatService gameSeatService;
    private final GameProps gameProps;

    @Transactional(readOnly = true)
    public List<GameDTO> getGamesList() {
        List<GameDTO> gameDTOList = new LinkedList<>();
        List<Game> games = gameRepo.findAllNotEndedGames(GameStatus.END.getStatus());

        List<GameSeat> gameSeats;
        for (Game game : games) {
            gameSeats = gameSeatService.getGameSeatsByGameId(game.getId());
            GameDTO gameDTO = GameConverter.toDTO(game, gameSeats.size());
            gameDTOList.add(gameDTO);
        }

        return gameDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    public Game createGame(long creatorPlayerId, CreateGameRequest createGameRequest) {
        var pot = potService.createPot();

        var game = Game.builder()
            .maxPlayers(createGameRequest.maxPlayers())
            .buyIn(createGameRequest.buyIn())
            .smallBlind(gameProps.getSmallBlind())
            .bigBlind(gameProps.getBigBlind())
            .name(createGameRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS.getStatus())
            .potId(pot.getId())
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .creatorPlayerId(creatorPlayerId)
            .dealerId(Util.DEFAULT_LONG_VALUE)
            .activePlayerId(Util.DEFAULT_LONG_VALUE)
            .build();

        var newGame = gameRepo.save(game);
        log.info("Created {} by player id {}", newGame, creatorPlayerId);

        return newGame;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGame(long gameId) {
        var game = gameRepo.findGameById(gameId);
        Long potId = game.getPotId();

        gameSeatService.deleteGameSeatByIdGameId(gameId);
        log.info("Removed game table with game id {}", gameId);

        gameRepo.deleteById(gameId);
        log.info("Removed game id {}", gameId);

        potService.deleteById(potId);
        log.info("Removed pot id {}", potId);
    }

    @Transactional(readOnly = true)
    public Game getGameById(Long gameId) {
        return gameRepo.findGameById(gameId);
    }

    /**
     * Updates player's status to {@link PlayerStatus#JOIN_THE_GAME} and chips.</br>
     * Created {@link GameSeat} entity
     * @param gameId {@link Game#getId()}
     * @param playerDetails {@link PlayerDetails} with info of this {@link User} and {@link Player}
     */
    public void joinPlayerToGame(long gameId, PlayerDetails playerDetails) {
        var game = gameRepo.findGameById(gameId);
        var player = playerDetails.getPlayer();
        long userId = playerDetails.getUser().getId();

//        TODO: think how to handle accidental disconnects
        var gameTable = gameSeatService.getGameSeatByGameIdAndPlayerId(gameId, player.getId());
        if (gameTable != null) {
            log.info("PLAYER {} CHIPS {}", player.getId(), player.getChips());
            player.setChips(player.getChips());
        } else {
            player.setChips(game.getBuyIn());

            gameTable = gameSeatService.createGameSeat(userId, player.getId(), game.getId());
        }
        log.info("PLAYER {} JOIN, GAME TABLE {}", player.getId(), gameTable);

        player.setStatus(PlayerStatus.JOIN_THE_GAME.getIntStatus());

        playerService.updatePlayer(player);

        log.info("User id {} joined, game id {}, game table id {}", userId, game.getId(), gameTable.getId());
    }

    public void updateGame(Game game) {
        gameRepo.save(game);
        log.info("Updated game {}", game);
    }

    @Transactional(readOnly = true)
    public List<Game> getListNonEndedGames() {
        return gameRepo.findAllNotEndedGames(GameStatus.END.getStatus());
    }
}
