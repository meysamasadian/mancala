package me.asadian.mancala.game.repository;

import me.asadian.mancala.game.model.BoardStateModel;
import me.asadian.mancala.game.model.GameModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class GameRepositoryTest {


    @Autowired
    GameRepository underTest;


    @Test
    public void testSaveCorrectly() {
        //given
        LocalDateTime now = LocalDateTime.now();
        String primaryPlayerToken = "pl_deASDd23d";
        String secondaryPlayerToken = "pl_oEsd65wd";
        List<Integer> primarySide = Arrays.asList(1, 2, 3, 4, 5, 6, 0);
        List<Integer> secondarySide = Arrays.asList(1, 2, 3, 4, 5, 6, 0);
        GameModel gameModel = initToSave(now, primaryPlayerToken, secondaryPlayerToken, primarySide, secondarySide);

        //when
        gameModel = underTest.save(gameModel);

        //then
        assertSavingResult(gameModel, now, primaryPlayerToken, secondaryPlayerToken, primarySide, secondarySide);
    }

    private void assertSavingResult(GameModel gameModel, LocalDateTime now, String primaryPlayerToken, String secondaryPlayerToken, List<Integer> primarySide, List<Integer> secondarySide) {
        Optional<GameModel> optionalLoadedGameModel = underTest.findById(gameModel.getId());
        assertTrue(optionalLoadedGameModel.isPresent());
        GameModel loadedGameModel = optionalLoadedGameModel.get();
        assertTrue(loadedGameModel.isActive());
        assertEquals(now, loadedGameModel.getCreatedAt());
        assertEquals(primaryPlayerToken, loadedGameModel.getPrimaryPlayer());
        assertEquals(secondaryPlayerToken, loadedGameModel.getSecondaryPlayer());
        assertEquals(PRIMARY, loadedGameModel.getCurrentState().getCurrentTurn());
        assertEquals(primarySide, loadedGameModel.getCurrentState().getPrimarySideSpots());
        assertEquals(secondarySide, loadedGameModel.getCurrentState().getSecondarySideSpots());
    }

    private static GameModel initToSave(LocalDateTime now, String primaryPlayerToken, String secondaryPlayerToken, List<Integer> primarySide, List<Integer> secondarySide) {
        return GameModel.builder()
                .active(true)
                .createdAt(now)
                .primaryPlayer(primaryPlayerToken)
                .secondaryPlayer(secondaryPlayerToken)
                .currentState(BoardStateModel.builder()
                        .currentTurn(PRIMARY)
                        .isGameOver(false)
                        .primarySideSpots(primarySide)
                        .secondarySideSpots(secondarySide)
                        .build())
                .build();
    }


    @Test
    public void testFetchWaitingForSecondaryPlayer() {
        //given
        LocalDateTime recentlyPeriod = LocalDateTime.now().minusMinutes(5);
        initListOfGames();

        //when
        List<GameModel> gameModels = underTest.findAllRecentlyWaitingForSecondaryPlayer(recentlyPeriod);

        //then
        assertLoadedGamesRecently(gameModels);

    }

    private void assertLoadedGamesRecently(List<GameModel> gameModels) {
        assertFalse(gameModels.isEmpty());
        assertEquals(2, gameModels.size());
    }

    private void initListOfGames() {
        GameModel model = GameModel.builder()
                .active(true)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .primaryPlayer("pr_test1")
                .build();
        underTest.save(model);

        model = GameModel.builder()
                .active(true)
                .primaryPlayer("pr_test2")
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .build();
        underTest.save(model);

        model = GameModel.builder()
                .active(true)
                .primaryPlayer("pr_test3")
                .createdAt(LocalDateTime.now())
                .build();
        underTest.save(model);

        model = GameModel.builder()
                .active(true)
                .primaryPlayer("pr_test4")
                .secondaryPlayer("pr_test5")
                .createdAt(LocalDateTime.now())
                .build();
        underTest.save(model);
    }


    @Test
    public void testFindByTokenAndActiveCorrectly() {
        //given
        List<String> gameTokens = initModelsForTestActivityStatus();

        //when
        Optional<GameModel> activeModel = underTest.findByTokenAndActiveTrue(gameTokens.get(0));
        Optional<GameModel> deactivatedModel = underTest.findByTokenAndActiveTrue(gameTokens.get(1));

        //then
        assertTrue(activeModel.isPresent());
        assertFalse(deactivatedModel.isPresent());
    }


    private List<String> initModelsForTestActivityStatus() {
        GameModel activeModel = GameModel.builder()
                .active(true)
                .token("game_1")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .primaryPlayer("pr_test1")
                .build();
        underTest.save(activeModel);

        GameModel deactivatedModel = GameModel.builder()
                .active(false)
                .token("game_2")
                .primaryPlayer("pr_test2")
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .build();
        underTest.save(deactivatedModel);

        return Arrays.asList(activeModel.getToken(), deactivatedModel.getToken());
    }

    @Test
    public void testFindByTokenAndActiveAndSecondaryPlayerIsNullCorrectly() {
        //given
        List<String> gameTokens = initModelsForTestSecondaryIsNull();

        //when
        Optional<GameModel> waitedGame = underTest.findByTokenAndActiveTrue(gameTokens.get(0));
        Optional<GameModel> startedGame = underTest.findByTokenAndActiveTrue(gameTokens.get(1));

        //then
        assertTrue(waitedGame.isPresent());
        assertFalse(startedGame.isPresent());
    }

    private List<String> initModelsForTestSecondaryIsNull() {
        GameModel waitedGame = GameModel.builder()
                .active(true)
                .token("game_1")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .primaryPlayer("pr_test1")
                .build();
        underTest.save(waitedGame);

        GameModel startedGame = GameModel.builder()
                .active(false)
                .token("game_2")
                .primaryPlayer("pr_test2")
                .secondaryPlayer("pr_test3")
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .build();
        underTest.save(startedGame);

        return Arrays.asList(waitedGame.getToken(), startedGame.getToken());
    }

    @Test
    public void testFindByActiveIsTrueAndPrimaryPlayer() {
        //given
        String playerUsername = "player1";
        initFindByActiveIsTrueAndPrimaryPlayer(playerUsername);

        //when
        Optional<GameModel> game = underTest.findByActiveTrueAndPrimaryPlayerOrSecondaryPlayer(playerUsername, playerUsername);

        //then
        assertTrue(game.isPresent());
    }

    private void initFindByActiveIsTrueAndPrimaryPlayer(String playerUsername) {
        underTest.save(GameModel.builder()
                .active(true)
                .token("game_1")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .primaryPlayer(playerUsername)
                .build());
    }


    @Test
    public void testFindByActiveIsTrueAndSecondaryPlayer() {
        //given
        String playerUsername = "player1";
        initFindByActiveIsTrueAndSecondaryPlayer(playerUsername);

        //when
        Optional<GameModel> game = underTest.findByActiveTrueAndPrimaryPlayerOrSecondaryPlayer(playerUsername, playerUsername);

        //then
        assertTrue(game.isPresent());
    }

    private void initFindByActiveIsTrueAndSecondaryPlayer(String playerUsername) {
        underTest.save(GameModel.builder()
                .active(true)
                .token("game_1")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .secondaryPlayer(playerUsername)
                .build());
    }

    @Test
    public void testExistsByTokenAndPrimaryPlayerIs() {
        //given
        String playerUsername = "player1";
        String gameToken = "game1";
        initExistsByTokenAndPrimaryPlayer(playerUsername, gameToken);

        //when
        boolean result = underTest.existsByTokenAndPrimaryPlayerOrSecondaryPlayer(gameToken, playerUsername, playerUsername);

        //then
        assertTrue(result);
    }


    private void initExistsByTokenAndPrimaryPlayer(String playerUsername, String gameToken) {
        underTest.save(GameModel.builder()
                .active(true)
                .token(gameToken)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .primaryPlayer(playerUsername)
                .build());
    }


    @Test
    public void testExistsByTokenAndSecondaryPlayerIs() {
        //given
        String playerUsername = "player1";
        String gameToken = "game1";
        initExistsByTokenAndSecondaryPlayer(playerUsername, gameToken);

        //when
        boolean result = underTest.existsByTokenAndPrimaryPlayerOrSecondaryPlayer(gameToken, playerUsername, playerUsername);

        //then
        assertTrue(result);
    }

    private void initExistsByTokenAndSecondaryPlayer(String playerUsername, String gameToken) {
        underTest.save(GameModel.builder()
                .active(true)
                .token(gameToken)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .secondaryPlayer(playerUsername)
                .build());
    }
}