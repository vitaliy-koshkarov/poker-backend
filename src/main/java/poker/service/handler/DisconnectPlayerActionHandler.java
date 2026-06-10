package poker.service.handler;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.game.GameEngine;
import poker.game.playeraction.PlayerActions;
import poker.model.Game;

@Component(value = PlayerActions.DISCONNECT)
@Log4j2
@ToString
public class DisconnectPlayerActionHandler implements PlayerActionHandler {
    @Override
    public void handleAction(GameEngine gameEngine, Game game, Long playerId) {
//        player's status updates in WebSocketEventListener
        log.info("Player id {} {} from game id {}", playerId, PlayerActions.DISCONNECT, game.getId());
    }
}
