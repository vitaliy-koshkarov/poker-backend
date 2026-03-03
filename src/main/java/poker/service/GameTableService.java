package poker.service;

import common.PlayerStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameTableRequest;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.model.GameTable;
import poker.repository.GameTableRepository;
import texasholdem.GameStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class GameTableService {
    private final GameTableRepository gameTableRepo;
    private final PotService potService;
    private final PlayerService playerService;
    private final PlayerTableService playerTableService;

    public GameTableService(GameTableRepository gameTableRepository, PotService potService,
                            PlayerService playerService, PlayerTableService playerTableService) {
        this.gameTableRepo = gameTableRepository;
        this.potService = potService;
        this.playerService = playerService;
        this.playerTableService = playerTableService;
    }

    public List<GameTableDTO> getGameTablesList() {
        var gameTablesList = gameTableRepo.findAllGamesByOrderByIdAsc();

        var gameTableDtoList = new LinkedList<GameTableDTO>();
        for (GameTable gameTable : gameTablesList) {
            gameTableDtoList.add(GameTableConverter.toDTO(gameTable));
        }

        return gameTableDtoList;
    }

    public void createGameTable(CreateGameTableRequest createGameTableRequest) {
        var pot = potService.createPot();

        var gt = GameTable.builder()
            .maxPlayers(createGameTableRequest.maxPlayers())
            .currentPlayers(Collections.emptySet())
            .buyIn(createGameTableRequest.buyIn())
            .name(createGameTableRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS)
            .potId(pot.getId())
            .build();

        var gameTable = gameTableRepo.save(gt);
        log.info("Game table created {}", gameTable);
    }

    public void removeGameTable(long gameTableId) {
        gameTableRepo.deleteById(gameTableId);
        log.info("Removed game table id {}", gameTableId);
    }

    public GameTable getGameTableById(Long gameTableId) {
        return gameTableRepo.findGameTableById(gameTableId);
    }

    public GameTable updateGameTableName(Long gameTableId, String name) {
        gameTableRepo.updateGameTableName(gameTableId, name);
        log.info("Game table name updated to {}", name);
        return gameTableRepo.findGameTableById(gameTableId);
    }

    public GameTable joinPlayerToGame(Long userId, Long tableId) {
        var player = playerService.getPlayerByUserId(userId);
        long playerId = player.getId();
        playerService.updatePlayerStatus(playerId, PlayerStatus.JOIN_THE_GAME);

        var gameTable = gameTableRepo.findGameTableById(tableId);
        Set<Long> currentPlayers = gameTable.getCurrentPlayers();
        currentPlayers.add(player.getId());
        long gameId = gameTable.getId();
        gameTableRepo.addPlayerToGame(gameId, currentPlayers);
        log.info("Add player id {} to game id {}", playerId, gameId);

        var playerTable = playerTableService.getPlayerTableByUserAndPlayerIds(userId, playerId);
        Set<Long> tableIds = playerTable.getTableIds();
        tableIds.add(tableId);
        playerTableService.updatePlayerTable(playerTable.getId(), tableIds, userId);

        return gameTableRepo.findGameTableById(tableId);
    }

    public void removePlayerFromTable(Long userId) {
        var player = playerService.getPlayerByUserId(userId);
        long playerId = player.getId();
        playerService.updatePlayerStatus(playerId, PlayerStatus.NOT_IN_GAME);
        log.info("Player id {} status updated to {}", playerId, PlayerStatus.NOT_IN_GAME);
//        TODO:
//         - find game table
//         - remove player id from that game (current player ids);
//         - change player status
    }
}
