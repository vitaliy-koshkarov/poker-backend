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

//        TODO: return correct value of current players
        var gameTableDtoList = new LinkedList<GameTableDTO>();
        for (GameTable gameTable : gameTablesList) {
            gameTableDtoList.add(
                new GameTableDTO(
                    gameTable.getId(),
                    0,
                    gameTable.getMaxPlayers(),
                    gameTable.getBuyIn(),
                    gameTable.getName())
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
}
