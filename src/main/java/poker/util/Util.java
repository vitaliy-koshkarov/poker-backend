package poker.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import poker.core.engine.GameEngine;
import poker.core.game.texasholdem.THPlayer;
import poker.core.player.GamePlayer;
import poker.model.PlayerDetails;

import java.security.Principal;

@Log4j2
public class Util {
    public static final long ZERO_LONG = 0;
    public static final int ZERO_INT = 0;
    public static final int INVALID_INT_VALUE = -1;

    public static PlayerDetails getPlayerDetailsFronCtx() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication {}", authentication);
        var playerDetails = (PlayerDetails) authentication.getPrincipal();
        log.debug("PlayerDetails {}", playerDetails);
        return playerDetails;
    }

    public static PlayerDetails getPlayerDetailsFromWebSocketSession(Principal principal) {
        var playerDetails = (PlayerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        log.debug("PlayerDetails WebSocket session {}", playerDetails);
        return playerDetails;
    }

    public static GamePlayer getPlayerById(GameEngine engine, long playerId) {
        for (GamePlayer gp : engine.getTable().getPlayers()) {
            if (gp.getId() == playerId) {
                return gp;
            }
        }
        return THPlayer.builder().build(); // temporary
    }
}
