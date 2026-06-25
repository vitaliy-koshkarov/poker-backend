package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import poker.game.GameEngineRegistry;

@Service("StartUpGameLoader")
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartUpGameLoader implements ApplicationRunner {
    private final GameService gameService;
    private final GameEngineRegistry gameEngineRegistry;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Loading games to the engine registry");

        var gamesList = gameService.getListNonEndedGames();
        gamesList.forEach(game -> {
            log.info("{}", game);
            gameEngineRegistry.registerGame(game);
        });
        log.info("Games loaded");
    }
}
