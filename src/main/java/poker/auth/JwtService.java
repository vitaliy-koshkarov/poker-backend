package poker.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.model.Role;

import java.security.SecureRandom;
import java.util.Date;

@Component
@Log4j2
public class JwtService {
    private final String secret = "temp-secret-key";
    private final long expirationMs = 86_400_000; // 24h

    public String generateToken(/*Authentication authentication*/String email, Role role) {
        var now = new Date();

        return Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs))
            .signWith(
                Jwts.SIG.HS256.key()
                    .random(new SecureRandom(secret.getBytes()))
                    .build(),
                Jwts.SIG.HS256
            )
            .compact();
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

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    private Claims extractClaims(String token) {
//        TODO: fix invalid token received after registration
        return Jwts.parser()
            .setSigningKey(secret.getBytes())
//            .decryptWith(Jwts.SIG.HS256.key()
//                .random(new SecureRandom(secret.getBytes()))
//                .build())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
