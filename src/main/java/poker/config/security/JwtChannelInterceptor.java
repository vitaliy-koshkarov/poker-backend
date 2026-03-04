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
import poker.model.PlayerDetails;
import poker.service.AuthService;
import poker.service.GameTableService;
import poker.util.Util;

import java.util.Set;

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

//        TODO: think about to make handler to CONNECT, DISCONNECT and SEND actions
        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                tryAuth(accessor);
//                TODO: Send message to other players about that player connected (update game state)
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
//                TODO: is there better way to handle disconnect?
                PlayerDetails playerDetails = Util.getPlayerDetailsFromWebSocketSession(accessor.getUser());
                disconnectPlayer(playerDetails);
//                TODO:
//                 1. Send message to other players about that player disconnected (update game state)
//                 2. Clear WebSession?
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

    private void disconnectPlayer(PlayerDetails playerDetails) {
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();
        Set<Long> tableIds = Set.copyOf(playerDetails.getTableIds());

//        TODO: 1 player - 1 game (table) yet
        gameTableService.removePlayerFromTable(userId, playerId, playerDetails);
        log.info("DISCONNECT user id {} player id {} left the games {}", userId, playerId, tableIds);
        log.info("DISCONNECT player details {}", playerDetails);
    }
}
