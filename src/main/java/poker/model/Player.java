package poker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "players")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;
}
