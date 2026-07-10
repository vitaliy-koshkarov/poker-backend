package poker.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poker.core.engine.GameEngine;
import poker.core.game.GamePot;
import poker.core.player.GamePlayer;
import poker.core.player.PlayerActionData;
import poker.service.*;
import poker.util.Util;

@Component("ALL_IN")
@RequiredArgsConstructor
@Log4j2
@ToString
public class AllInPlayerActionHandler implements DBPlayerActionHandler {
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
        GamePlayer player = Util.getPlayerById(gameEngine, playerId);

        gameService.updateActivePlayer(gameId, gameEngine.getTable().getActivePlayerId());

        playerService.updateStatusAndChipsAndCurrentBet(
            player.getId(), player.getStatus(), player.getChips(), player.getCurrentBet());

        GamePot pot = gameEngine.getTable().getPot();
        int currentBet = player.getCurrentBet();
        playerBetService.updatePlayerBet(playerId, pot.getId(), currentBet);

        potService.updatePotTotal(pot.getId(), pot.getTotal());

        long eventId = gameEventService.createAndSaveEvent(gameEngine, pad);

        log.info("Player id {} {} game id {} pot id {} event id {}",
            playerId, pad.getPlayerAction(), gameId, pot.getId(), eventId);

        return true;
    }
}
