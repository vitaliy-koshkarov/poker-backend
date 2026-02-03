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
        log.info("Get list of tables");
        return tableService.getTables();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTable(@RequestBody CreateTableRequest createTableRequest) {
        log.info("Create table: {}", createTableRequest);

        var createdTable = tableService.addTable(createTableRequest);
        log.info("Table created {}\r\n", createdTable);

        return ResponseEntity.ok().build();
    }
}
