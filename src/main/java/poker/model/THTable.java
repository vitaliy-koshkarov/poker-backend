package poker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@jakarta.persistence.Table(schema = "public", name = "texas_holdem_tables")
@Getter
@Setter
@ToString
public class THTable {
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
}
