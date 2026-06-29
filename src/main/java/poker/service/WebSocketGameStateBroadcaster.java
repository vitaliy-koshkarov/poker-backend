package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import poker.dto.game.GameStateDTO;
import poker.core.player.PlayerAction;

@Service("WebSocketGameStateBroadcaster")
@Log4j2
@RequiredArgsConstructor
@ToString
public class WebSocketGameStateBroadcaster {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void broadcast(GameStateDTO gameStateDTO, PlayerAction playerAction) {
//        TODO: refactoring 'destination' way of passing and passing to this method
        long gameId = gameStateDTO.gameDTO().id();
        String destination = "/topic/gameTable/" + gameId;
        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        simpMessagingTemplate.convertAndSend(destination, message);

        log.info("Broadcast {} for game id {} response: {}", playerAction.getActionName(), gameId, gameStateDTO);
    }
}
