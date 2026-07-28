package poker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.websocket")
@Getter
@Setter
public class WebSocketProps {
    private String broadcastDestination;
}
