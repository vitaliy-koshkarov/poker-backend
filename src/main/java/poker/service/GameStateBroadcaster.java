package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import poker.dto.game.GameStateDTO;

@Service("GameStateResponseGenerator")
@Log4j2
@RequiredArgsConstructor
@ToString
public class GameStateBroadcaster {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void broadcast(GameStateDTO gameStateDTO) {
//        TODO: refactoring destination
        long gameId = gameStateDTO.gameDTO().id();
        String destination = "/topic/gameTable/" + gameId;
        Message<GameStateDTO> message = new GenericMessage<>(gameStateDTO);
        simpMessagingTemplate.convertAndSend(destination, message);
//        var gameStateDTO = playerActionHandlerService.getCurrentState(gameId);

        log.info("Broadcast for game id {} response: {}", gameId, gameStateDTO);
    }
}
