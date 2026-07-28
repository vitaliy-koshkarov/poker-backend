package poker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poker.dto.auth.RegistrationRequest;
import poker.service.PlayerService;
import poker.service.UserService;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidationService {
    private final UserService userService;
    private final PlayerService playerService;

    public void validateRegistration(RegistrationRequest request) {
        if (userService.isUserExistsByEmail(request.email())) {
            log.error("Email {} already exists", request.email());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email " + request.email() + " taken");
        }

        if (playerService.isPlayerExistsByNickname(request.nickname())) {
            log.error("Nickname {} already exists", request.nickname());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname " + request.nickname() + " taken");
        }
    }
}
