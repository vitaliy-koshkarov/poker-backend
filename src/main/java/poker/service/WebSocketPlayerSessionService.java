package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.PlayerGameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class WebSocketPlayerSessionService {
//    TODO: store player sessions somewhere and restore them after app reboot?

    /**
     * Key - web socket session id. Value = {@link PlayerGameSession}
     */
    private final Map<String, PlayerGameSession> webSocketSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, Long playerId, Long gameId, String sessionId) {
        webSocketSessions.put(sessionId, new PlayerGameSession(userId, playerId, gameId));
        log.info("Add web socket session id {}, user id {}, player id {}, game id {}", sessionId, userId, playerId, gameId);
    }

    public PlayerGameSession getPlayerSession(String sessionId) {
        return webSocketSessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        var playerSession = webSocketSessions.remove(sessionId);
        log.info("Removed web socket session id {}, user id {}, player id {}, game id {}",
            sessionId, playerSession.userId(), playerSession.playerId(), playerSession.gameId());
    }
}
