package poker.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@ToString
public class PlayerDetails implements UserDetails {
    private final User user;

    @Setter
    private Player player;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Set<Long> tableIds;

    public PlayerDetails(User user, Player player, Set<Long> tableIds) {
        this.user = user;
        this.player = player;
        this.email = user.getEmail();
        this.authorities = List.of((new SimpleGrantedAuthority(user.getRole().name())));
        this.tableIds = tableIds;
    }

    @Override
    public String getUsername() {
        return player.getNickname();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
}
