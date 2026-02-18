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
import poker.auth.JwtIssuer;

@Component
@Log4j2
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtIssuer jwtIssuer;

    public JwtChannelInterceptor(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    @Nullable
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("Full message: {}", message);
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.debug("STOMP accessor: {}", accessor);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            var authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("Native header: {}", authorizationHeader);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new AccessDeniedException("Missing token");
            }

            var jwt = authorizationHeader.substring(7);
            if (!jwtIssuer.isTokenValid(jwt)) {
                throw new AccessDeniedException("Invalid token " + jwt);
            }

            Authentication authentication = jwtIssuer.authenticate(jwt);
            long userId = jwtIssuer.extractUserId(jwt);
            var userEmail = jwtIssuer.extractUserEmail(jwt);
            log.info("JWT WebSocket CONNECT auth user id {}, email {}", userId, userEmail);

            accessor.setUser(authentication);
        }

        return message;
    }
}
