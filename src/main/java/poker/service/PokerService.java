package poker.service;

import lombok.extern.log4j.Log4j2;
import poker.model.Greeting;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PokerService {
    public Greeting greeting(String someString) {
        log.info("Service layer. Input string: {}", someString);
        return new Greeting("Hello, " + someString);
    }
}
