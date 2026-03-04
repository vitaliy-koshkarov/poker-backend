package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(schema = "public", name = "player_tables")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PlayerTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    /**
     * {@link GameTable#id} of the tables the player is sitting at
     */
    @Column(name = "table_ids", nullable = false)
    private Set<Long> tableIds;
}
