package poker.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import poker.model.PlayerDetails;

@Component
@Log4j2
public class Util {
    public static PlayerDetails getPlayerDetailsFronCtx() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication {}", authentication);
        var playerDetails = (PlayerDetails) authentication.getPrincipal();
        log.debug("PlayerDetails {}", playerDetails);
        return playerDetails;
    }
}
