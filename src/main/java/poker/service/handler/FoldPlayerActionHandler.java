package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;

@Component("FOLD")
@Log4j2
@ToString
public class FoldPlayerActionHandler implements DBPlayerActionHandler {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(long playerId, GameEngine gameEngine) {
        log.info("Player id {} {} game id {}", playerId, PlayerAction.FOLD.getActionName(), gameEngine.getTable().getId());
        return true;
    }
}
