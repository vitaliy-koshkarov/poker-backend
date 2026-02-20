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
import poker.repository.PlayerRepository;
import poker.repository.UserRepository;
import poker.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final UserRepository userRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public AuthController(UserRepository userRepository,
                          PlayerRepository playerRepository,
                          @Qualifier("pokerPasswordEncoder")
                          PasswordEncoder passwordEncoder,
                          AuthenticationService authenticationService) {
        this.userRepo = userRepository;
        this.playerRepo = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegistrationRequest regReq) {
        log.info("Register user with email {}, nickname {}", regReq.email(), regReq.nickname());

        if (userRepo.existsByEmail(regReq.email())) {
            log.error("Email {} already exists", regReq.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");
        }

        if (playerRepo.existsByNickname(regReq.nickname())) {
            log.error("Nickname {} already exists", regReq.nickname());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname taken");
        }

        var player = Player.builder()
            .nickname(regReq.nickname())
            .status(PlayerStatus.NOT_IN_GAME)
            .chips(0)
            .currentBet(0)
            .build();

        var user = User.builder()
            .email(regReq.email())
            .password(passwordEncoder.encode(regReq.password()))
            .role(Role.ROLE_USER)
            .player(player)
            .build();

        var registeredUser = userRepo.save(user);

        log.info("Saved user {}", registeredUser);

        var token = authenticationService.generateToken(
            registeredUser.getId(), registeredUser.getEmail(), registeredUser.getRole());

        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginReq) {
        log.info("Login user {}", loginReq.email());

        var user = userRepo.findByEmail(loginReq.email())
            .orElseThrow(() -> {
                log.error("Not found user {}", loginReq.email());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email does not exist");
            });

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            log.error("Passwords do not match for user {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        Long userId = user.getId();
        var token = authenticationService.generateToken(userId, user.getEmail(), user.getRole());

        log.info("Successful login user id {}", userId);

        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        var userId = authenticationService.getUserIdFromJwt(request);
        log.info("Logout user {}", userId);
        return ResponseEntity.ok().build();
    }
}
