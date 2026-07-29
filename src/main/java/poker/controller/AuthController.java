package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poker.dto.auth.AuthResponse;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.CurrentPlayerIdResponse;
import poker.dto.auth.RegistrationRequest;
import poker.service.AuthService;
import poker.service.UserService;
import poker.service.ValidationService;
import poker.util.Util;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final ValidationService validationService;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(ValidationService validationService,
                          UserService userService,
                          AuthService authService) {
        this.validationService = validationService;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        log.info("Register user with email {}, nickname {}", request.email(), request.nickname());

//        TODO: add email parsing validation
        validationService.validateRegistrationRequest(request);

        var user = userService.createUser(request.email(), request.password(), request.nickname());

        String token = "";
        if (user != null) {
            token = authService.generateToken(user);
        }

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginReq) {
        log.info("Login user {}", loginReq.email());
//        TODO: add email parsing validation

        var user = userService.getUserByEmail(loginReq.email());

        validationService.validateLogin(user, loginReq);

        var token = authService.generateToken(user);
        log.info("Successful login user id {}, email {}", user.getId(), user.getEmail());

        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout user {}", Util.getPlayerDetailsFronCtx().getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getCurrentPlayerId")
    public CurrentPlayerIdResponse getCurrentPlayerId() {
        log.info("Request getCurrentPlayerId");
        return CurrentPlayerIdResponse.builder()
            .currentPlayerId(Util.getPlayerDetailsFronCtx()
                .getPlayer()
                .getId()
            )
            .build();
    }
}
