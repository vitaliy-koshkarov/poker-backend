package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import poker.model.Greeting;
import org.springframework.web.bind.annotation.*;
import poker.service.PokerService;

@RestController
@RequestMapping("poker")
@Log4j2
public class PokerController {
    private final PokerService pokerService;

    public PokerController(PokerService pokerService) {
        this.pokerService = pokerService;
    }

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(value = "name", required = false) String someString) {
        log.info("Controller layer. Request parameters: {}", someString);
        return pokerService.greeting(someString);
    }

    @MessageMapping("/action")
    public void handleAction(String userName, String playerAction) {
        log.info("PokerController. Request param: {}, {}", userName, playerAction);
        pokerService.handlePlayerAction(playerAction);
    }
}
