package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

    @Column(name = "buy_in", nullable = false)
    private Integer buyIn;

    @Column(name = "small_blind", nullable = false)
    private Integer smallBlind;

    @Column(name = "big_blind", nullable = false)
    private Integer bigBlind;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "pot_id", nullable = false)
    private Long potId;

    @Column(name = "creator_player_id", nullable = false)
    private Long creatorPlayerId;

    @Column(name = "dealer_id", nullable = false)
    private Long dealerId;

    @Column(name = "active_player_id", nullable = false)
    private Long activePlayerId;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "ended_at")
    private Timestamp endedAt;
}
