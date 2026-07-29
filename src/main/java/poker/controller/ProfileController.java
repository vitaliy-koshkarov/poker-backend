package poker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poker.dto.profile.ProfileInfoRequest;
import poker.dto.profile.ProfileInfoResponse;
import poker.dto.profile.UpdatePasswordRequest;
import poker.service.PlayerService;
import poker.service.UserService;
import poker.service.ValidationService;
import poker.util.Util;

@RestController
@RequestMapping("/api/profile")
@Log4j2
public class ProfileController {
    private final ValidationService validationService;
    private final UserService userService;
    private final PlayerService playerService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(ValidationService validationService,
                             UserService userService,
                             PlayerService playerService,
                             PasswordEncoder passwordEncoder) {
        this.validationService = validationService;
        this.userService = userService;
        this.playerService = playerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/getProfileInfo")
    public ResponseEntity<?> getProfileInfo() {
        var playerDetails = Util.getPlayerDetailsFronCtx();
        log.info("getProfileInfo user id {}", playerDetails.getUser().getId());

        var profileInfoResponse = ProfileInfoResponse.builder()
            .email(playerDetails.getUser().getEmail())
            .nickname(playerDetails.getPlayer().getNickname())
            .build();
        log.debug("getProfileInfo response {}", profileInfoResponse);

        return ResponseEntity.ok(profileInfoResponse);
    }

    @PostMapping("/updateProfileInfo")
    public ResponseEntity<?> updateProfileInfo(@RequestBody ProfileInfoRequest request) {
        validationService.validateUpdProfileInfo(request);

        playerService.updateProfileInfo(request);

        return ResponseEntity.ok("Profile updated");
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        var playerDetails = Util.getPlayerDetailsFronCtx();
        log.info("Change password request, user id {}", playerDetails.getUser().getId());

        validationService.validateChangePassword(request, playerDetails);

        String newPassword = passwordEncoder.encode(request.newPassword());
        userService.updateUserPassword(playerDetails.getUser().getId(), newPassword);

        return ResponseEntity.ok().body("Password updated");
    }
}
