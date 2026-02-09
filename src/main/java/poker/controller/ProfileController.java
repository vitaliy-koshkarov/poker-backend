package poker.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poker.auth.JwtIssuer;
import poker.dto.profile.ProfileInfoRequest;
import poker.dto.profile.ProfileInfoResponse;
import poker.dto.profile.UpdatePasswordRequest;
import poker.repository.UserRepository;

@RestController
@RequestMapping("/api/profile")
@Log4j2
public class ProfileController {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwtIssuer;

    public ProfileController(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             JwtIssuer jwtIssuer) {
        this.userRepo = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtIssuer = jwtIssuer;
    }

    @GetMapping("/getProfileInfo")
    public ProfileInfoResponse getProfileInfo(HttpServletRequest request) {
        Long userId = jwtIssuer.getUserIdFromJwt(request);
        log.info("getProfileInfo user {}", userId);
        log.debug("User id {}", userId);

        var user = userRepo.findById(userId)
            .orElseThrow(() -> {
                log.error("Not found user {}", userId);
                return new UsernameNotFoundException("Not found user " + userId);
            });

        var profileInfoResponse = ProfileInfoResponse.builder()
            .email(user.getEmail())
            .nickname(user.getPlayer().getNickname())
            .build();
        log.debug("Response {}", profileInfoResponse);
        return profileInfoResponse;
    }

    @PostMapping("/updateProfileInfo")
    public ProfileInfoResponse updateProfileInfo(@RequestBody ProfileInfoRequest req) {
        var email = req.email();
        var user = userRepo.findByEmail(email)
            .orElseThrow(() -> {
                log.error("Not found user {}", email);
                return new UsernameNotFoundException("Not found user " + email);
            });

        user.getPlayer().setNickname(req.nickname());
        userRepo.save(user);

        log.info("Update user info {}", user);

        var profileInfoResponse = ProfileInfoResponse.builder()
            .email(user.getEmail())
            .nickname(user.getPlayer().getNickname())
            .build();
        log.debug("Response {}", profileInfoResponse);

        return profileInfoResponse;
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(HttpServletRequest httpServletRequest,
                                            @RequestBody UpdatePasswordRequest req) {
        Long userId = jwtIssuer.getUserIdFromJwt(httpServletRequest);

        var user = userRepo.findById(userId)
            .orElseThrow(() -> {
                log.error("Not found user {}", userId);
                return new UsernameNotFoundException("Not found user " + userId);
            });

        var oldPass = req.oldPassword();
        var newPass = req.newPassword();

        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            log.error("Passwords do not match for user {}", userId);
            return ResponseEntity.badRequest().body("Wrong old password");
        }

        if (oldPass.equals(newPass)) {
            log.info("The passwords are no different");
            return ResponseEntity.badRequest().body("The new password must be different from the current one");
        }

        user.setPassword(passwordEncoder.encode(newPass));
        userRepo.save(user);
        log.info("Update password for user {}", userId);

        return ResponseEntity.ok().body("Successfully updated password");
    }
}
