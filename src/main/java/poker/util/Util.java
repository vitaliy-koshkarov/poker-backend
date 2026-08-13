package poker.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import poker.model.PlayerDetails;

import java.security.Principal;

@Log4j2
public class Util {
    public static final long ZERO_LONG = 0;
    public static final int ZERO_INT = 0;
    public static final int INVALID_INT_VALUE = -1;
    public static final int MIN_PLAYERS = 2;
    public static final int TEXAS_HOLDEM_DECK_SIZE = 52;

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
}
