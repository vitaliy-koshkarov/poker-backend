package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import poker.dto.game.GameTableConverter;
import poker.dto.game.GameTableDTO;
import poker.model.PlayerDetails;
import poker.service.GameTableService;

@Controller
@Log4j2
public class WebSocketPokerController {
    private final GameTableService gameTableService;

    public WebSocketPokerController(GameTableService gameTableServicer) {
        this.gameTableService = gameTableServicer;
    }

    @SubscribeMapping("/gameTable/{id}")
    public GameTableDTO subscribe(@DestinationVariable("id") Long tableId,
                                  @AuthenticationPrincipal Authentication authentication) throws Exception {
        Long userId = ((PlayerDetails) authentication.getPrincipal()).getId();

        log.info("SUBSCRIBE user id {}, game table id {}", userId, tableId);

        log.debug("SUBSCRIBE authentication {}", authentication);

        var gameTable = gameTableService.joinPlayerToGame(userId, tableId);
        log.info("User id {} joined to game id {}", userId, tableId);

        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("SUBSCRIBE {}", gameTableDTO);

        return gameTableDTO;
    }

    @MessageMapping("/table/{id}")
    @SendTo("/topic/gameTable/{id}")
    public Message<GameTableDTO> handleMessage(@DestinationVariable("id") Long tableId,
                                               @Payload String newGameTableName,
                                               @AuthenticationPrincipal Authentication authentication) {
        Long userId = ((PlayerDetails) authentication.getPrincipal()).getId();

        log.info("SEND user id {}, game table id {}, new table name {}", userId, tableId, newGameTableName);

        log.debug("SEND authentication {}", authentication);

        var gameTable = gameTableService.updateGameTableName(tableId, newGameTableName);
        var gameTableDTO = GameTableConverter.toDTO(gameTable);
        log.info("SEND {}", gameTableDTO);

        Message<GameTableDTO> message = new GenericMessage<>(gameTableDTO);
        log.info("SEND {}", message);

        return message;
    }
}
