package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.game.GamePot;
import poker.core.game.texasholdem.THPlayer;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.service.*;

@Component("BET")
@RequiredArgsConstructor
@Log4j2
@ToString
public class BetPlayerActionHandler implements DBPlayerActionHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerBetService playerBetService;
    private final PotService potService;
    private final GameEventService gameEventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAction(GameEngine gameEngine, PlayerActionData pad) {
        long gameId = gameEngine.getTable().getId();
        long playerId = pad.getPlayerDetails().getPlayer().getId();

        gameService.updateActivePlayer(gameId, gameEngine.getTable().getActivePlayerId());

        GamePlayer player = getPlayerById(gameEngine, playerId);
        int currentBet = player.getCurrentBet();
        playerService.updateStatusAndChipsAndCurrentBet(player.getId(), player.getStatus(), player.getChips(), currentBet);

        GamePot pot = gameEngine.getTable().getPot();
        playerBetService.updatePlayerBet(playerId, pot.getId(), currentBet);

        potService.updatePotTotal(pot.getId(), pot.getTotal());

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} game id {} pot id {} event id {}",
            playerId, pad.getPlayerAction(), gameId, pot.getId(), eventId);

        return true;
    }

    private GamePlayer getPlayerById(GameEngine engine, long playerId) {
        for (GamePlayer gp : engine.getTable().getPlayers()) {
            if (gp.getId() == playerId) {
                return gp;
            }
        }
        return THPlayer.builder().build(); // temporary
    }
}
