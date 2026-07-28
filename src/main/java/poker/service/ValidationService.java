package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.RegistrationRequest;
import poker.model.User;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidationService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PlayerService playerService;

    public void validateRegistrationRequest(RegistrationRequest request) {
//        TODO: add email parsing validation

        if (userService.isUserExistsByEmail(request.email())) {
            log.info("Email {} already exists", request.email());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email " + request.email() + " taken");
        }

        if (playerService.isPlayerExistsByNickname(request.nickname())) {
            log.info("Nickname {} already exists", request.nickname());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname " + request.nickname() + " taken");
        }
    }

    public void validateLogin(User user, LoginRequest loginReq) {
        if (user == null) {
            log.info("User not found by email {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "User with email " + loginReq.email() + " not found");
        }

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            log.info("Passwords do not match for user {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }
}
