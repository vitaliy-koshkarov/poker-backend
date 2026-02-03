package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.dto.table.CreateTableRequest;
import poker.model.THTable;
import poker.repository.THTableRepository;

@Service
@Log4j2
public class TableService {
    private final THTableRepository THTableRepo;

    public TableService(THTableRepository THTableRepository) {
        this.THTableRepo = THTableRepository;
    }

    public Object addTable(CreateTableRequest createTableRequest) {
        THTable table = new THTable();
        table.setCurrentPlayers(0);
        table.setMaxPlayers(createTableRequest.maxPlayers());
        table.setBuyIn(createTableRequest.buyIn());
        table.setName(createTableRequest.name());

        THTable savedTable = THTableRepo.save(table);
        log.info("Table saved {}", savedTable);

        return savedTable;
    }
}
