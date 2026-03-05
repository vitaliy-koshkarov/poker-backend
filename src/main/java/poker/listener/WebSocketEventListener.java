package poker.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import poker.dto.game.GameTableDTO;
import poker.model.PlayerDetails;
import poker.service.GameTableService;

import java.util.Set;

@Component
@Log4j2
public class WebSocketEventListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameTableService gameTableService;

    public WebSocketEventListener(SimpMessagingTemplate simpMessagingTemplate, GameTableService gameTableService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameTableService = gameTableService;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        log.info("Disconnect event");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("Accessor: {}", accessor);

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        if (authentication == null) {
            return;
        }

        var playerDetails = (PlayerDetails) authentication.getPrincipal();

        Set<Long> tableIds = disconnectPlayer(playerDetails);
        for (Long tableId : tableIds) {
            var gameTableDTO = GameTableDTO.builder()
                .id(-1L)
                .currentPlayers(-1)
                .maxPlayers(-1)
                .name("test")
                .buyIn(-1)
                .build();

            Message<GameTableDTO> message = new GenericMessage<>(gameTableDTO);
            String destination = "/topic/gameTable";
            log.debug("Message {}", message);
            log.info("Sending message about disconnect to {}/{}", destination, tableId);
            simpMessagingTemplate.convertAndSend("/topic/gameTable/" + tableId, message);
        }
    }

    private Set<Long> disconnectPlayer(PlayerDetails playerDetails) {
//        TODO: return game state
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();
        Set<Long> tableIds = Set.copyOf(playerDetails.getTableIds());

//        TODO: 1 player - 1 game (table) yet
        gameTableService.removePlayerFromTable(userId, playerId, playerDetails);
        log.info("Disconnect event user id {} player id {} left the games {}", userId, playerId, tableIds);
        log.debug("Disconnect event player details {}", playerDetails);

        return tableIds;
    }
}
