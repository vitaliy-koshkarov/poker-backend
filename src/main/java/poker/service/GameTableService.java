package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.game.CreateGameTableRequest;
import poker.dto.game.GameTableDTO;
import poker.model.GameTable;
import poker.model.Pot;
import poker.repository.GameTableRepository;
import texasholdem.GameStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class GameTableService {
    private final GameTableRepository gameTableRepo;

    public GameTableService(GameTableRepository gameTableRepository) {
        this.gameTableRepo = gameTableRepository;
    }

    public List<GameTableDTO> getGameTablesList() {
        var gameTablesList = gameTableRepo.findAllGamesByOrderByIdAsc();

        var gameTableDtoList = new LinkedList<GameTableDTO>();
        for (GameTable gameTable : gameTablesList) {
            gameTableDtoList.add(
                GameTableDTO.builder()
                    .id(gameTable.getId())
                    .currentPlayers(gameTable.getCurrentPlayers().size())
                    .maxPlayers(gameTable.getMaxPlayers())
                    .buyIn(gameTable.getBuyIn())
                    .name(gameTable.getName())
                    .build()
            );
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

    public GameTableDTO getGameTableById(Long id) throws Exception {
        GameTable gameTable = gameTableRepo.findById(id).orElseThrow(() -> {
            log.error("Fail to get game table data by id {}", id);
            return new Exception("Fail to get game table data by id " + id);
        });

        return GameTableDTO.builder()
            .id(gameTable.getId())
            .currentPlayers(gameTable.getCurrentPlayers().size())
            .maxPlayers(gameTable.getMaxPlayers())
            .buyIn(gameTable.getBuyIn())
            .name(gameTable.getName())
            .build();
    }
}
