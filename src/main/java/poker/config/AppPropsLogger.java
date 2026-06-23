package poker.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AppPropsLogger implements ApplicationRunner {
    private final GameProps gameProps;

    public AppPropsLogger(GameProps gameProps) {
        this.gameProps = gameProps;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Game properties loaded:");
        log.info("smallBlind={}", gameProps.getSmallBlind());
        log.info("bigBlind={}", gameProps.getBigBlind());
    }
}
