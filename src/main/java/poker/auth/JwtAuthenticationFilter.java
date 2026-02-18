package poker.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtIssuer jwtIssuer;

    public JwtAuthenticationFilter(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        if (!jwtIssuer.isTokenValid(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = jwtIssuer.authenticate(jwt);

        ((AbstractAuthenticationToken) auth).setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        long userId = jwtIssuer.extractUserId(jwt);
        var userEmail = jwtIssuer.extractUserEmail(jwt);
//        var userDetailsUserName = ((User) auth.getPrincipal()).getUsername();

        log.info("Auth user with id {}, email {}", userId, userEmail);

        filterChain.doFilter(request, response);
    }
}
