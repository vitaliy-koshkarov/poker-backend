package poker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "players_bets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PlayerBet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pot_id", nullable = false)
    private Long potId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "player_bet", nullable = false)
    private Integer playerBet;
}
