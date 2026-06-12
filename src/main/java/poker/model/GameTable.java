package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(schema = "public", name = "game_tables")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GameTable {
//    TODO: think to rename the entity to avoid misunderstanding its purpose

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    /**
     * {@link Game#getId()} of the tables the player is sitting at
     */
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
