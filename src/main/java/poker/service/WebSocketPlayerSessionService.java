package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class WebSocketPlayerSessionService {
//    TODO: store player sessions somewhere and restore them after app reboot?

    /**
     * Key - web socket session id. Value = {@link PlayerSession}
     */
    private final Map<String, PlayerSession> webSocketSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, Long playerId, Long gameId, String sessionId) {
        webSocketSessions.put(sessionId, new PlayerSession(userId, playerId, gameId));
        log.info("Add web socket session id {}, user id {}, player id {}, game id {}", sessionId, userId, playerId, gameId);
    }

    public PlayerSession getPlayerSession(String sessionId) {
        return webSocketSessions.get(sessionId);
    }

    public PlayerSession removeSession(String sessionId) {
        var playerSession = webSocketSessions.remove(sessionId);
        log.info("Removed web socket session id {}, user id {}, player id {}, game id {}",
            sessionId, playerSession.userId(), playerSession.playerId(), playerSession.gameId());
        return playerSession;
    }
}
