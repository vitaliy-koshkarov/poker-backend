package poker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import poker.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Log4j2
public class AuthService {
//    TODO: use values from config file
    private final String secret = "temp-more-long-enough-not-super-secret-key";
    private final long expirationMs = 86_400_000; // 24h
    private final PokerUserDetailService puds;

    public AuthService(PokerUserDetailService pokerUserDetailService) {
        this.puds = pokerUserDetailService;
    }

    public String generateToken(User user) {
        var now = new Date();
        Long userId = user.getId();

        String jwt = Jwts.builder()
            .id(Long.toString(userId))
            .subject(user.getEmail())
            .claim("role", user.getRole())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
            .compact();

        log.info("Generated JWT for user {}", userId);

        return jwt;
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            log.error("Invalid token received", ex);
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getId());
    }

    public String extractUserEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserIdFromJwt(HttpServletRequest request) {
        var jwt = request.getHeader("Authorization").substring(7);
        return extractUserId(jwt);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Authentication authenticate(String jwt) {
        long userId = extractUserId(jwt);

        var playerDetails = puds.getUserById(userId);

        return new UsernamePasswordAuthenticationToken(
            playerDetails,
            null,
            playerDetails.getAuthorities()
        );
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
