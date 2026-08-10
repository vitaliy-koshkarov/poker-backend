package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.core.engine.GameEngine;
import poker.core.game.GamePot;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerAction;
import poker.core.player.PlayerActionData;
import poker.service.*;

@Service
@Log4j2
@RequiredArgsConstructor
@ToString
public class CallPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final PotService potService;
    private final GameEventService gameEventService;

    @Override
    public PlayerAction supportsPlayerAction() {
        return PlayerAction.CALL;
    }

    @Override
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.table().getId();
        long playerId = pad.getPlayerId();

        gameService.updateActivePlayer(gameId, gameEngine.table().getActivePlayerId());

        GamePlayer player = gameEngine.table().getPlayerById(playerId);
        int currentBet = player.getCurrentBet();
        playerService.updateStatusAndChipsAndCurrentBet(player.getId(), player.getStatus(), player.getChips(), currentBet);

        GamePot pot = gameEngine.table().getPot();
        playerBetService.updatePlayerBet(playerId, pot.getId(), currentBet);

        potService.updatePotTotal(pot.getId(), pot.getTotal());

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} game id {} pot id {} event id {}",
            playerId, pad.getPlayerAction(), gameId, pot.getId(), eventId);

        return true;
    }
}
