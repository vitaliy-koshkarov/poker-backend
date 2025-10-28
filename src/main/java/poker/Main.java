package poker;

import poker.controller.PokerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import poker.handler.ActionHandler;
import poker.service.PokerService;

@SpringBootApplication
@ComponentScan(basePackageClasses = PokerController.class)
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public PokerService pokerService() {
        return new PokerService(actionHandler());
    }

    @Bean
    public ActionHandler actionHandler() {
        return new ActionHandler();
    }
}
