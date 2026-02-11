package poker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import poker.handler.WebSocketGameHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketGameHandler webSocketGameHandler;

    public WebSocketConfig(WebSocketGameHandler webSocketGameHandler) {
        this.webSocketGameHandler = webSocketGameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketGameHandler, "/ws/game")
            .setAllowedOrigins("*");
    }
}