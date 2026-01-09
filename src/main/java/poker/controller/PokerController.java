package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import poker.model.ResponseMessage;
import poker.model.RequestMessage;
import poker.service.PokerService;

@RestController
//@RequestMapping("poker")
@Log4j2
public class PokerController {
    private final PokerService pokerService;

    public PokerController(PokerService pokerService) {
        this.pokerService = pokerService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ResponseMessage greeting(RequestMessage message) {
        log.info("Controller. Request param: {}", message);
        var response = pokerService.greeting(message.getName());
        log.info("Response: {}", response);
        return response;
    }
}
