package me.asadian.mancala.game.model;


import jakarta.persistence.*;
import lombok.*;
import me.asadian.mancala.shared.constants.game.Side;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(indexes = {
    @Index(name = "uniqueIndex", columnList = "token", unique = true)
}
)

public class GameModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String token;
    private String primaryPlayer;
    private String secondaryPlayer;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private boolean active;
    private Side winner;

    @OneToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "state_id")
    private BoardStateModel currentState;



}
