package poker.service.event;

import poker.core.player.GamePlayer;
import poker.util.Util;

import java.util.List;

public class EventUtil {
    public static int getPlayerStatus(List<GamePlayer> gamePlayers, long playerId) {
        for (GamePlayer gp : gamePlayers) {
            if (gp.getId() == playerId) {
                return gp.getStatus().getIntStatus();
            }
        }
        return Util.INVALID_INT_VALUE; // TODO: throw ex and handle it above
    }
}
