package poker.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import poker.model.Role;
import poker.service.PokerUserDetailService;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Log4j2
public class JwtIssuer {
//    TODO: use values from config file
    private final String secret = "temp-more-long-enough-not-super-secret-key";
    private final long expirationMs = 86_400_000; // 24h
    private final PokerUserDetailService puds;

    public JwtIssuer(PokerUserDetailService pokerUserDetailService) {
        this.puds = pokerUserDetailService;
    }

    public String generateToken(Long userId, String email, Role role) {
        var now = new Date();

        String jwt = Jwts.builder()
            .id(Long.toString(userId))
            .subject(email)
            .claim("role", role)
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

    public Long getUserIdFromJwt(HttpServletRequest request) {
        var jwt = request.getHeader("Authorization").substring(7);
        return extractUserId(jwt);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Authentication authenticate(String jwt) {
        Long userId = extractUserId(jwt);

        var userDetails = puds.findUserById(userId);

        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
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
