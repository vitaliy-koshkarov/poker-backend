package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poker.dto.TableDTO;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@Log4j2
public class TableController {

    @GetMapping
    public List<TableDTO> getTables() {
        log.info("Call from front: {}", Instant.now());
        return List.of(
            new TableDTO(1, 0, 6),
            new TableDTO(2, 1, 6),
            new TableDTO(3, 3, 6)
        );
    }
}
