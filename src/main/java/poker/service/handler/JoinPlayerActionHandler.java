package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;

@Component(value = PlayerActions.JOIN_GAME)
@Log4j2
@ToString
public class JoinPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Long playerId) {
//        player updates in SUBSCRIBE Controller method
        log.info("Player id {} {} game id {}", playerId, PlayerActions.JOIN_GAME, game.getId());
    }
}
