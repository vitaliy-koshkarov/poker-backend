package poker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "pots")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Pot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total", unique = true, nullable = false)
    private Integer total;
}
