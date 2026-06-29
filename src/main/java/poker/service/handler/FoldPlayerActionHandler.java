package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;

@Component("FOLD")
@Log4j2
@ToString
public class FoldPlayerActionHandler implements DBPlayerActionHandler {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        log.info("Player id {} {} game id {}",
            pad.getPlayerDetails().getPlayer().getId(), PlayerAction.FOLD.getActionName(), gameEngine.getTable().getId());

        return true;
    }
}
