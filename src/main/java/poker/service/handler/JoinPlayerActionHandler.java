package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.service.GameService;

@Component("JOIN")
@Log4j2
@ToString
@RequiredArgsConstructor
public class JoinPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        gameService.joinPlayerToGame(pad);

        log.info("Player id {} {} game id {}",
            pad.getPlayerDetails().getPlayer().getId(), PlayerAction.JOIN_GAME.getActionName(), gameEngine.getTable().getId());

        return true;
    }
}
