package poker.model;

import jakarta.persistence.*;
import lombok.*;
import texasholdem.GameState;

@Entity
@Table(schema = "public", name = "games")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "current_players", nullable = false)
    private Integer currentPlayers;

    @Column(name = "buy_in", nullable = false)
    private Integer buyIn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameState status;
}
