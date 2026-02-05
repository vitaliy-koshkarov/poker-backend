package poker.model;

import common.PlayerStatus;
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

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PlayerStatus status;

    @Column(name = "chips", nullable = false)
    private Integer chips;

    @Column(name = "current_bet", nullable = false)
    private Integer currentBet;
}
