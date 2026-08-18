package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("StartUpGameLoader")
@Log4j2
@RequiredArgsConstructor
@ToString
public class StartUpGameLoader implements ApplicationRunner {
//    private final GameService gameService;
//    private final GameRegistry gameRegistry;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Loading games to the engine registry");

//        TODO: Do I need to restore games?
//              If yes, then think how to recover games.
//              What if need to recover tens and hundreds of thousands of games?
        var gamesList = Collections.emptyList(); // gameService.getListNonEndedGames();
//        gamesList.forEach(game -> {
//            log.info("{}", game);
//            gameRegistry.recoverGame(game);
//        });
        log.info("Loaded {} games", gamesList.size());
    }
}
