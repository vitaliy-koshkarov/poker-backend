package poker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat/chat";
    }

//    for testing JWT
    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }
}
