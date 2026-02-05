package poker.model;

import jakarta.persistence.*;
import lombok.*;
import texasholdem.GameStatus;

import java.util.Set;

@Entity
@Table(schema = "public", name = "game_tables")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GameTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "current_players_ids", nullable = false)
    private Set<Long> currentPlayers;

    @Column(name = "buy_in", nullable = false)
    private Integer buyIn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;
}
