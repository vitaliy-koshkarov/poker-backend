package poker.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AppPropsLogger implements ApplicationRunner {
    private final JwtProps jwtProps;
    private final WebSocketProps webSocketProps;
    private final GameProps gameProps;

    public AppPropsLogger(JwtProps jwtProps, WebSocketProps webSocketProps, GameProps gameProps) {
        this.jwtProps = jwtProps;
        this.webSocketProps = webSocketProps;
        this.gameProps = gameProps;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("App properties:");
        log.info("jwt.expirationMs={}", jwtProps.getExpirationMs());
        log.info("websocket.broadcastDestination={}", webSocketProps.getBroadcastDestination());
        log.info("smallBlind={}", gameProps.getSmallBlind());
        log.info("bigBlind={}", gameProps.getBigBlind());
    }
}
