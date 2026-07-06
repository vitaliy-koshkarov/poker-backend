package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(schema = "public", name = "players_seats")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PlayerSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    /**
     * {@link Game#getId()} of the table the player is sitting at
     */
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    /**
     * Seat number at the table in engine. Could be 0
     */
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
