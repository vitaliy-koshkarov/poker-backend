package poker.config.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import poker.service.AuthService;
import poker.service.GameTableService;
import poker.util.Util;

@Component
@Log4j2
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final AuthService authService;
    private final GameTableService gameTableService;

    public JwtChannelInterceptor(AuthService authService,
                                 GameTableService gameTableService) {
        this.authService = authService;
        this.gameTableService = gameTableService;
    }

    @Nullable
    @Override
    public Message<?> preSend(Message<?> inboundMessage, MessageChannel channel) {
        log.debug("Full message: {}", inboundMessage);
        var accessor = MessageHeaderAccessor.getAccessor(inboundMessage, StompHeaderAccessor.class);
        log.debug("STOMP accessor: {}", accessor);
        log.debug("Channel {}", channel);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                tryAuth(accessor);
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                var playerDetails = Util.getPlayerDetailsFromWebSocketSession(accessor.getUser());
                Long userId = playerDetails.getId();
                gameTableService.removePlayerFromTable(userId);
                log.info("User {} left the game", userId);
//                TODO: clear WebSession?
            }
        }

        return inboundMessage;
    }

    private void tryAuth(StompHeaderAccessor accessor) {
        var authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        log.debug("Native header: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing token");
        }

        var jwt = authorizationHeader.substring(7);
        if (!authService.isTokenValid(jwt)) {
            throw new AccessDeniedException("Invalid token " + jwt);
        }

        Authentication authentication = authService.authenticate(jwt);
        long userId = authService.extractUserId(jwt);
        var userEmail = authService.extractUserEmail(jwt);

        accessor.setUser(authentication);
        log.info("WebSocket authenticate user with id {}, email {}", userId, userEmail);
    }
}
