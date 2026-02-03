package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import poker.dto.table.CreateTableRequest;
import poker.dto.table.TableDTO;
import poker.model.THTable;
import poker.repository.THTableRepository;

import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class TableService {
    private final THTableRepository THTableRepo;

    public TableService(THTableRepository THTableRepository) {
        this.THTableRepo = THTableRepository;
    }

    public List<TableDTO> getTables() {
        var thTablesList = THTableRepo.findAll(Sort.by(Sort.Order.asc("id")));

        List<TableDTO> tables = new LinkedList<>();
        for (THTable t : thTablesList) {
            tables.add(new TableDTO(t.getId(), t.getCurrentPlayers(), t.getMaxPlayers(), t.getBuyIn(), t.getName()));
        }

        return tables;
    }

    public THTable addTable(CreateTableRequest createTableRequest) {
        THTable table = new THTable();
        table.setCurrentPlayers(0);
        table.setMaxPlayers(createTableRequest.maxPlayers());
        table.setBuyIn(createTableRequest.buyIn());
        table.setName(createTableRequest.name());

        THTable savedTable = THTableRepo.save(table);
        log.info("Table saved {}", savedTable);

        return savedTable;
    }

    public void removeTable(long id) {
        THTableRepo.deleteById(id);
        log.info("Removed table with id {}", id);
    }
}
