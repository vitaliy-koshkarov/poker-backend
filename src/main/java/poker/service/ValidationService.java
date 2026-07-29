package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poker.core.engine.GameEngine;
import poker.core.engine.GameEngineRegistry;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.RegistrationRequest;
import poker.dto.game.CreateGameRequest;
import poker.dto.profile.ProfileInfoRequest;
import poker.dto.profile.UpdatePasswordRequest;
import poker.model.PlayerDetails;
import poker.model.User;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidationService {
    private final PasswordEncoder passwordEncoder;
    private final GameEngineRegistry gameEngineRegistry;
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

    public void validateUpdProfileInfo(ProfileInfoRequest request) {
        String nickname = request.nickname();
        if (playerService.isPlayerExistsByNickname(nickname)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname " + nickname + " taken");
        }
    }

    public void validateChangePassword(UpdatePasswordRequest request, PlayerDetails playerDetails) {
        String currentPassword = request.currentPassword();

        if (!passwordEncoder.matches(currentPassword, playerDetails.getUser().getPassword())) {
            log.info("Passwords do not match, user id {}", playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong current password");
        }

        if (currentPassword.equals(request.newPassword())) {
            log.info("The passwords must be different, user id {}", playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The new password must be different from the current one");
        }
    }

    public void validateCreatingGame(CreateGameRequest request) {
//        todo: add check for buyIn and maxPlayers values
        String gameName = request.name();
        for (GameEngine engine : gameEngineRegistry.getGameEngineCollection()) {
            if (engine.table().getName().equals(gameName)) {
                log.info("Game with name {} already exists", gameName);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Game with name " + gameName + " already exists");
            }
        }
    }
}
