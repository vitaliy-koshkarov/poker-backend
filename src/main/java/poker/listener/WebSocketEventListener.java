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
import poker.dto.game.GameDTO;
import poker.model.PlayerDetails;
import poker.service.GameService;
import poker.service.GameTableService;
import poker.service.PlayerSessionService;

@Component
@Log4j2
public class WebSocketEventListener {
    private final PlayerSessionService playerSessionService;
    private final GameService gameService;
    private final GameTableService gameTableService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketEventListener(PlayerSessionService playerSessionService, GameService gameService,
                                  GameTableService gameTableService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerSessionService = playerSessionService;
        this.gameService = gameService;
        this.gameTableService = gameTableService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        log.info("Disconnect event");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("Accessor: {}", accessor);
//        String accessorSessionId = accessor.getSessionId();
//        log.info("Accessor session id {}", accessorSessionId);

        var authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        if (authentication == null) {
            return;
        }

        var playerDetails = (PlayerDetails) authentication.getPrincipal();

        String sessionId = event.getSessionId();
        log.info("Disconnect session id {}", sessionId);

        Long gameId = disconnectPlayer(playerDetails, sessionId);

        var game = gameService.getGameById(gameId);
        var gameTables = gameTableService.getGameTablesById(gameId);

        var gameDTO = GameDTO.builder()
            .id(game.getId())
            .currentPlayers(gameTables.size())
            .maxPlayers(game.getMaxPlayers())
            .name(game.getName())
            .buyIn(game.getBuyIn())
            .build();

        Message<GameDTO> message = new GenericMessage<>(gameDTO);
        String destination = "/topic/gameTable/" + gameId;

        log.debug("Message {}", message);
        log.info("Sending message to {} about player id {} disconnect",
            destination, playerDetails.getPlayer().getId());

        simpMessagingTemplate.convertAndSend(destination, message);
    }

    private Long disconnectPlayer(PlayerDetails playerDetails, String sessionId) {
//        TODO: return game state?
        Long userId = playerDetails.getUser().getId();
        Long playerId = playerDetails.getPlayer().getId();

//        TODO: 1 player - 1 game (table) yet
        var playerSession = playerSessionService.remove(sessionId);
        log.info("Disconnect event, player session {}", playerSession);

        Long gameId = playerSession.gameId();
        gameService.removePlayerFromGame(userId, playerId, gameId, playerDetails);

        log.info("Disconnect event user id {}, player id {} left game id {}", userId, playerId, gameId);
        log.debug("Disconnect event player details {}", playerDetails);

        return gameId;
    }
}
