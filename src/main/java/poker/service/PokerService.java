package poker.service;

import lombok.extern.log4j.Log4j2;
import poker.handler.ActionHandler;
import poker.model.Greeting;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PokerService {
    private final ActionHandler actionHandler;

    public PokerService(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public Greeting greeting(String someString) {
        log.info("Service layer. Input string: {}", someString);
        return new Greeting("Hello, " + someString);
    }

    public void handlePlayerAction(String playerAction) {
        log.info("PokerService, handle {}", playerAction);
        actionHandler.handleAction(playerAction);
    }
}
