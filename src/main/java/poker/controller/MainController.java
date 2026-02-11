package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class MainController {
//    TODO: remove controller after test
    @GetMapping("/main")
    public String index() {
        log.info("/ main page request");
        return "index";
    }

    @GetMapping("/chat")
    public String chat() {
        log.info("/chat request");
        return "chat/chat";
    }

//    for testing JWT
    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }
}
