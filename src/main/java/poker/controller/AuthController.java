package poker.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import poker.model.Role;
import poker.model.User;
import poker.dto.auth.AuthResponse;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.RegistrationRequest;
import poker.repository.UserRepository;
import poker.auth.JwtIssuer;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwtIssuer;

    public AuthController(UserRepository userRepo,
                          PasswordEncoder passwordEncoder,
                          JwtIssuer jwtIssuer) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtIssuer = jwtIssuer;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegistrationRequest regReq) {
        log.info("Register user with email {}, nickname {}", regReq.email(), regReq.nickname());

        if (userRepo.existsByEmail(regReq.email())) {
            log.error("Email {} already exists", regReq.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");
        }

        if (userRepo.existsByNickname(regReq.nickname())) {
            log.error("Nickname {} already exists", regReq.nickname());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname taken");
        }

        var newUser = new User(
            regReq.email(),
            regReq.nickname(),
            passwordEncoder.encode(regReq.password()),
            Role.ROLE_USER);

        var registeredUser = userRepo.save(newUser);

        log.info("Saved user {}", registeredUser);

        var token = jwtIssuer.generateToken(
            registeredUser.getId(), registeredUser.getEmail(), registeredUser.getRole());

        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginReq) {
        log.info("Logining user {}", loginReq.email());

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
        var token = jwtIssuer.generateToken(userId, user.getEmail(), user.getRole());

        log.info("Successful login user id {}", userId);

        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        var userId = jwtIssuer.getUserIdFromJwt(request);
        log.info("Logout user {}", userId);
        return ResponseEntity.ok().build();
    }
}
