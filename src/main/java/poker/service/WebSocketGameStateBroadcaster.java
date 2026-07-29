package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import poker.config.WebSocketProps;
import poker.dto.game.GameDTO;
import poker.core.player.PlayerAction;

@Service("WebSocketGameStateBroadcaster")
@Log4j2
@RequiredArgsConstructor
@ToString
public class WebSocketGameStateBroadcaster {
    private final WebSocketProps webSocketProps;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void broadcast(GameDTO gameDTO, PlayerAction playerAction) {
        long gameId = gameDTO.id();
        String destination = webSocketProps.getBroadcastDestination() + gameId;

        Message<GameDTO> message = new GenericMessage<>(gameDTO);
        simpMessagingTemplate.convertAndSend(destination, message);

        log.info("Broadcast to {}", destination);
        log.info("Broadcast {} game id {} response: {}", playerAction.getActionName(), gameId, gameDTO);
    }
}
