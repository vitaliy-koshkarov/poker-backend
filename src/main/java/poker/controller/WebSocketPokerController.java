package poker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import poker.service.GameTableService;

@Controller
@Log4j2
public class WebSocketPokerController {
    private final SimpMessagingTemplate smt;
    private final GameTableService gameTableService;
    private final ObjectMapper om;

    public WebSocketPokerController(SimpMessagingTemplate simpMessagingTemplate,
                                    GameTableService gameTableService,
                                    ObjectMapper objectMapper) {
        this.smt = simpMessagingTemplate;
        this.gameTableService = gameTableService;
        this.om = objectMapper;
    }

    @MessageMapping("/{id}")
    public void greeting(@DestinationVariable Long id) throws Exception {
        log.info("Received game table id {}", id);

        var gameTableDTO = gameTableService.getGameTableById(id);
        log.info("Return: {}", gameTableDTO);

        byte[] s = om.writeValueAsBytes(gameTableDTO);
        Message<byte[]> message = new GenericMessage<>(s);
        log.info("Message: {}", message);

        smt.send("/gameTable/" + id, message);
    }
}
