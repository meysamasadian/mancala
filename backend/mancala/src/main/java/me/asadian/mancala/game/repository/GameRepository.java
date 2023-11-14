package me.asadian.mancala.game.repository;

import me.asadian.mancala.game.model.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameModel, Long> {


    @Query("SELECT game FROM GameModel game WHERE secondaryPlayer is NULL AND createdAt >=(:recentlyPeriod) ")
    List<GameModel> findAllRecentlyWaitingForSecondaryPlayer(LocalDateTime recentlyPeriod);

    Optional<GameModel> findByToken(String token);

    @Query("SELECT g FROM GameModel g " +
            "WHERE g.active = true " +
            "AND g.token = (:token) ")
    Optional<GameModel> findByTokenAndActiveTrue(String token);

    Optional<GameModel> findByTokenAndActiveTrueAndSecondaryPlayerNull(String token);

    @Query("SELECT g FROM GameModel g " +
            "WHERE g.active = true " +
            "AND (g.primaryPlayer = (:primaryPlayer) " +
            "OR g.secondaryPlayer = (:secondaryPlayer) )")
    Optional<GameModel> findByActiveTrueAndPrimaryPlayerOrSecondaryPlayer(String primaryPlayer, String secondaryPlayer);

    boolean existsByTokenAndPrimaryPlayerOrSecondaryPlayer(String token, String primaryPlayer, String secondaryPlayer);
}
