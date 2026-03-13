package poker.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "chips", nullable = false)
    private Integer chips;

    @Column(name = "current_bet", nullable = false)
    private Integer currentBet;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
