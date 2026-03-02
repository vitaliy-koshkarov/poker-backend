package poker.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@ToString(exclude = {"password"})
public class PlayerDetails implements UserDetails {
    private final Long id;
    private final String email;
    /**
     * Nickname of the user. {@link Player#nickname}
     */
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public PlayerDetails(User user, Player player) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = player.getNickname();
        this.password = user.getPassword();
        this.authorities = List.of((new SimpleGrantedAuthority(user.getRole().name())));
    }
}
