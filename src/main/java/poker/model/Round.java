package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(schema = "public", name = "rounds")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "last_aggressor_player_id", nullable = false)
    private Long lastAggressorPlayerId;

    @Column(name = "last_max_bet", nullable = false)
    private Integer lastMaxBet;

    @Column(name = "players_to_act", nullable = false)
    private Set<Long> playersToAct;
}
