package poker.controller;

import common.PlayerStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import poker.model.Player;
import poker.model.Role;
import poker.model.User;
import poker.dto.auth.AuthResponse;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.RegistrationRequest;
import poker.service.AuthService;
import poker.service.PlayerService;
import poker.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final UserService userService;
    private final PlayerService playerService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(UserService userService,
                          PlayerService playerService,
                          @Qualifier("pokerPasswordEncoder") PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.userService = userService;
        this.playerService = playerService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegistrationRequest regReq) {
        log.info("Register user with email {}, nickname {}", regReq.email(), regReq.nickname());

        if (userService.isUserExistsByEmail(regReq.email())) {
            log.error("Email {} already exists", regReq.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");
        }

        if (playerService.isPlayerExistsByNickname(regReq.nickname())) {
            log.error("Nickname {} already exists", regReq.nickname());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname taken");
        }

        var player = Player.builder()
            .nickname(regReq.nickname())
            .status(PlayerStatus.NOT_IN_GAME)
            .chips(0)
            .currentBet(0)
            .build();

        var newPlayer = playerService.createPlayer(player);

        var user = User.builder()
            .email(regReq.email())
            .password(passwordEncoder.encode(regReq.password()))
            .role(Role.ROLE_USER)
            .playerId(newPlayer.getId())
            .build();

        var registeredUser = userService.createUser(user);

        log.info("Saved user {}", registeredUser);

        var token = authService.generateToken(
            registeredUser.getId(), registeredUser.getEmail(), registeredUser.getRole());

        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginReq) {
        log.info("Login user {}", loginReq.email());

        var user = userService.getUserByEmail(loginReq.email());

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            log.error("Passwords do not match for user {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        Long userId = user.getId();
        var token = authService.generateToken(userId, user.getEmail(), user.getRole());

        log.info("Successful login user id {}, email {}", userId, user.getEmail());

        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
//        TODO: think about of make access to logout only to authorized user
        var userId = authService.extractUserIdFromJwt(request);
        log.info("Logout user {}", userId);
        return ResponseEntity.ok().build();
    }
}
