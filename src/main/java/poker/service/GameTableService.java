package poker.service;

import common.PlayerStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameTableRequest;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.model.GameTable;
import poker.model.Pot;
import poker.repository.GameTableRepository;
import texasholdem.GameStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Log4j2
public class GameTableService {
    private final GameTableRepository gameTableRepo;
    private final UserService userService;
    private final PlayerService playerService;

    public GameTableService(GameTableRepository gameTableRepository,
                            UserService userService,
                            PlayerService playerService) {
        this.gameTableRepo = gameTableRepository;
        this.userService = userService;
        this.playerService = playerService;
    }

    public List<GameTableDTO> getGameTablesList() {
        var gameTablesList = gameTableRepo.findAllGamesByOrderByIdAsc();

        var gameTableDtoList = new LinkedList<GameTableDTO>();
        for (GameTable gameTable : gameTablesList) {
            gameTableDtoList.add(GameTableConverter.toDTO(gameTable));
        }

        return gameTableDtoList;
    }

    public void addGameTable(CreateGameTableRequest createGameTableRequest) {
        var pot = Pot.builder()
            .total(0)
            .build();

        var gameTable = GameTable.builder()
            .maxPlayers(createGameTableRequest.maxPlayers())
            .currentPlayers(Collections.emptySet())
            .buyIn(createGameTableRequest.buyIn())
            .name(createGameTableRequest.name())
            .status(GameStatus.WAITING_FOR_PLAYERS)
            .pot(pot)
            .build();

        var savedGameTable = gameTableRepo.save(gameTable);
        log.info("Game table saved {}", savedGameTable);
    }

    public void removeGameTable(long id) throws Exception {
        var game = gameTableRepo.findById(id).orElseThrow(() -> new Exception("asd"));
        log.info("Removed game table {}", game);
        gameTableRepo.deleteById(id);
    }

    public GameTable getGameTableById(Long id) throws Exception {
        return gameTableRepo.findById(id).orElseThrow(() -> {
            log.error("Fail to get game table data by id {}", id);
            return new Exception("Fail to get game table data by id " + id);
        });
    }

    public GameTable updateGameTableName(Long id, String name) {
        gameTableRepo.updateGameTableNameById(id, name);
//        TODO: improve somehow
        AtomicReference<GameTable> gameTableRef = new AtomicReference<>();
        gameTableRepo.findById(id).ifPresent(gameTableRef::set);

        return gameTableRef.get();
    }

    public GameTable joinPlayerToGame(Long userId, Long tableId) throws Exception {
        var user = userService.getUserById(userId);
        var player = user.getPlayer();
        player.setStatus(PlayerStatus.JOIN_THE_GAME);
        var updatedPlayer = playerService.updatePlayer(player);
        log.info("Player updated {}", updatedPlayer);

        var gameTable = gameTableRepo.findById(tableId)
            .orElseThrow(() -> {
                log.error("Can not find game by id {}", tableId);
                return new Exception("Can not find game by id " + tableId);
            });

        Set<Long> currentPlayers = gameTable.getCurrentPlayers();
        currentPlayers.add(player.getId());
        var updatedGameTable = gameTableRepo.save(gameTable);
        log.info("GameTable updated {}", updatedGameTable);
        return updatedGameTable;
    }
}
