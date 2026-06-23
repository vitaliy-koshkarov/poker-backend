package poker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.game")
@Getter
@Setter
public class GameProps {
    private int smallBlind;
    private int bigBlind;
}
