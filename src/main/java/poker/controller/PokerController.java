package poker.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import poker.model.Greeting;
import org.springframework.web.bind.annotation.*;
import poker.service.PokerService;

@RestController
@RequestMapping("poker")
@AllArgsConstructor
@Log4j2
public class PokerController {
    private final PokerService pokerService;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(value = "name", required = false) String someString) {
        log.info("Controller layer. Request parameters: {}", someString);
        return pokerService.greeting(someString);
    }
}
