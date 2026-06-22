package poker.model.event;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;

@Entity
@Table(schema = "public", name = "game_events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Type(JsonBinaryType.class)
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private GameEventData gameEventData;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
