package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.service.GameTableService;

@Controller
@Log4j2
public class WebSocketPokerController {
    private final GameTableService gameTableService;

    public WebSocketPokerController(GameTableService gameTableServicer) {
        this.gameTableService = gameTableServicer;
    }

    @SubscribeMapping("/gameTable/{id}")
    public GameTableDTO subscribe(@DestinationVariable Long id) throws Exception {
        log.info("Subscribe user to game table with id {}", id);

        var gameTable = gameTableService.getGameTableById(id);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("Subscribe message: {}", gameTableDTO);

        return gameTableDTO;
    }

    @MessageMapping("/table/{id}")
    @SendTo("/topic/gameTable/{id}")
    public Message<GameTableDTO> handleMessage(@DestinationVariable Long id,
                                               @Payload String newGameTableName) {
        log.info("Handle message of game table with id {}", id);
        log.info("New game table name `{}`", newGameTableName);

        var gameTable = gameTableService.updateGameTableName(id, newGameTableName);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("Handle message {}", gameTableDTO);

        Message<GameTableDTO> message = new GenericMessage<>(gameTableDTO);
        log.info("Handle message: {}", message);

        return message;
    }
}
