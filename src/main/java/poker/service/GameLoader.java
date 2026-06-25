package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import poker.game.GameRegistry;

@Service("GameLoader")
@Log4j2
@RequiredArgsConstructor
@ToString
public class GameLoader implements ApplicationRunner {
    private final GameService gameService;
    private final GameRegistry gameRegistry;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Load games to the registry engine");

        var gamesList = gameService.getListNonEndedGames();
        gamesList.forEach(game -> {
            log.info("{}", game);
            gameRegistry.registerGame(game);
        });
        log.info("Games loaded");
    }
}
