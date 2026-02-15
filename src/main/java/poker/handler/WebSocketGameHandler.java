package poker.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import poker.service.PokerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class WebSocketGameHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions;
    private final PokerService pokerService;

    public WebSocketGameHandler(ObjectMapper objectMapper, PokerService pokerService) {
        this.objectMapper = objectMapper;
        this.sessions = new ConcurrentHashMap<>();
        this.pokerService = pokerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var sessionId = session.getId();
        log.info("Received message from session {}", sessionId);
        sessions.put(sessionId, session);
        log.info("Connected session {}", sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        var sessionId = session.getId();
        log.info("Handling message for session {}", sessionId);

        JsonNode json = objectMapper.readTree((String) message.getPayload());
        Long gameTableId = json.get("gameTableId").asLong();

        var gameTableDTO = pokerService.handleAction(sessionId, gameTableId);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(gameTableDTO)));

        log.info("Sent message for session {}", sessionId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketSession removedSession = sessions.remove(session.getId());
        log.info("Disconnected session : {}", removedSession.getId());
    }
}
