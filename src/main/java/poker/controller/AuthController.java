package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import poker.dto.auth.AuthResponse;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.GetCurrentPlayerIdResponse;
import poker.dto.auth.RegistrationRequest;
import poker.service.AuthService;
import poker.service.UserService;
import poker.util.Util;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final ValidationService validationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(ValidationService validationService,
                          UserService userService,
                          @Qualifier("pokerPasswordEncoder") PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.validationService = validationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        log.info("Register user with email {}, nickname {}", request.email(), request.nickname());

//        TODO: handle error when empty token sends. Do not redirect to profile page
        validationService.validateRegistration(request);

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

        var user = userService.getUserByEmail(loginReq.email());

        if (user == null) {
            log.info("User not found by email {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found");
        }

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            log.error("Passwords do not match for user {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        var token = authService.generateToken(user);

        log.info("Successful login user id {}, email {}", user.getId(), user.getEmail());

        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
//        TODO: check id from JWT
        Long userId = Util.getPlayerDetailsFronCtx().getUser().getId();
        log.info("Logout user {}", userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getCurrentPlayerId")
    public GetCurrentPlayerIdResponse getCurrentPlayerId() {
        log.info("Request getCurrentPlayerId");
        var playerDetails = Util.getPlayerDetailsFronCtx();
        return GetCurrentPlayerIdResponse.builder()
            .currentPlayerId(playerDetails.getPlayer().getId())
            .build();
    }
}
