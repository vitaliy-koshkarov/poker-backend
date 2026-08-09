package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

@Service
@Log4j2
@ToString
public class CallPlayerActionHandler implements DBPlayerActionHandler {
    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CALL;
    }

    @Override
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
//        TODO: implement
        return false;
    }
}
