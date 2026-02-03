package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.dto.table.CreateTableRequest;
import poker.dto.table.TableDTO;
import poker.service.TableService;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@Log4j2
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<TableDTO> getTables() {
        log.info("Get tables");
        return List.of(
            new TableDTO(1, 0, 6),
            new TableDTO(2, 1, 6),
            new TableDTO(3, 3, 6)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTable(@RequestBody CreateTableRequest createTableRequest) {
        log.info("Create table: {}", createTableRequest);

        var createdTable = tableService.addTable(createTableRequest);
        log.info("Table created {}\r\n", createdTable);

        return ResponseEntity.ok().build();
    }
}
