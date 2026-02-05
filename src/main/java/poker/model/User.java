package poker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"password"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(targetEntity = Player.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private Player player;
}
