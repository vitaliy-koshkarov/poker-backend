package poker.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import texasholdem.THEngine;
import texasholdem.THTable;

@Component
@Log4j2
public class ActionHandler {
    private final THEngine engine;
    private final THTable table;

    public ActionHandler() {
        this.table = new THTable(1, 4, 10, 20);
        this.engine = new THEngine(this.table);
    }

    public void handleAction(String playerAction) {
//        TODO: handle game actions
        log.info("ActionHandler, handle player's action: {}", playerAction);
    }
}
