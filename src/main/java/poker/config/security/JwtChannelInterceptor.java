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
import poker.service.AuthenticationService;

@Component
@Log4j2
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final AuthenticationService authenticationService;

    public JwtChannelInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
            if (!authenticationService.isTokenValid(jwt)) {
                throw new AccessDeniedException("Invalid token " + jwt);
            }

            Authentication authentication = authenticationService.authenticate(jwt);
            long userId = authenticationService.extractUserId(jwt);
            var userEmail = authenticationService.extractUserEmail(jwt);

            accessor.setUser(authentication);
            log.info("WebSocket authenticate user with id {}, email {}", userId, userEmail);
        }

        return message;
    }
}
