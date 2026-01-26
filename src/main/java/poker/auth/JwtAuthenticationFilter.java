package poker.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import poker.service.PokerUserDetailService;

import java.io.IOException;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtIssuer jwtIssuer;
    private final PokerUserDetailService pods;

    public JwtAuthenticationFilter(JwtIssuer jwtIssuer,
                                   PokerUserDetailService pokerUserDetailService) {
        this.jwtIssuer = jwtIssuer;
        this.pods = pokerUserDetailService;
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

        Long userId = jwtIssuer.extractUserId(jwt);

        var userDetails = pods.findUserById(userId);

        var authentication = new UsernamePasswordAuthenticationToken(
            userDetails,null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Auth user id {}", userId);

        filterChain.doFilter(request, response);
    }
}
