package poker.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.model.User;
import poker.service.AuthService;
import poker.dto.profile.ProfileInfoRequest;
import poker.dto.profile.ProfileInfoResponse;
import poker.dto.profile.UpdatePasswordRequest;
import poker.service.PlayerService;
import poker.service.UserService;

@RestController
@RequestMapping("/api/profile")
@Log4j2
public class ProfileController {
    private final UserService userService;
    private final PlayerService playerService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public ProfileController(UserService userService,
                             PlayerService playerService,
                             PasswordEncoder passwordEncoder,
                             AuthService authService) {
        this.userService = userService;
        this.playerService = playerService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @GetMapping("/getProfileInfo")
    public ProfileInfoResponse getProfileInfo() {
        var playerDetails = ((PlayerDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal());
        long userId = playerDetails.getId();

        log.info("getProfileInfo user {}", userId);

        var player = playerService.getPlayerByUserId(userId);

        var profileInfoResponse = ProfileInfoResponse.builder()
            .email(playerDetails.getEmail())
            .nickname(player.getNickname())
            .build();
        log.info("getProfileInfo response {}", profileInfoResponse);
        return profileInfoResponse;
    }

    @PostMapping("/updateProfileInfo")
    public ResponseEntity<?> updateProfileInfo(@RequestBody ProfileInfoRequest req) {
        var playerDetails = (PlayerDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
        long userId = playerDetails.getId();

        Player player = playerService.getPlayerByUserId(userId);
        long playerId = player.getId();
        String newNickname = req.nickname();
        playerService.updateProfileInfo(playerId, newNickname);
        log.info("Update player id {} profile info", playerId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(HttpServletRequest httpServletRequest,
                                            @RequestBody UpdatePasswordRequest req) {
        Long userId = authService.extractUserIdFromJwt(httpServletRequest);
        log.info("Change password request user id {}", userId);

        User user = userService.getUserById(userId);

//        TODO: Check and compare:
//         1. User.id from JWT and SecurityContextHolder
//         2. current pass from SecurityContextHolder with current pass from request
        var currentPass = req.currentPassword();
        var newPass = req.newPassword();

        if (!passwordEncoder.matches(currentPass, user.getPassword())) {
            log.error("Passwords do not match for user {}", userId);
            return ResponseEntity.badRequest().body("Wrong current password");
        }

        if (currentPass.equals(newPass)) {
            log.info("The passwords are no different");
            return ResponseEntity.badRequest().body("The new password must be different from the current one");
        }

        String newPassword = passwordEncoder.encode(newPass);
        userService.updateUserPassword(userId, newPassword);
        log.info("Update password user id {}", userId);

        return ResponseEntity.ok().body("Successfully updated password");
    }
}
