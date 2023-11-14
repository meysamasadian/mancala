package me.asadian.mancala.player.service;

import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static me.asadian.mancala.player.service.PlayerRegistrationValidator.*;
import static me.asadian.mancala.player.service.PlayerRegistrationValidator.ValidationResult.SUCCESS;
import static me.asadian.mancala.player.service.PlayerRegistrationValidator.isRepeatPasswordValid;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerRegistrationValidatorTest {

    @ParameterizedTest
    @MethodSource("provideInvalidUsername")
    public void testInvalidUsername(PlayerRegistrationRequest request) {
        PlayerRegistrationValidator.ValidationResult result = PlayerRegistrationValidator.isUsernameValid().apply(request);
        assertEquals(
                PlayerRegistrationValidator.ValidationResult.USERNAME_NOT_VALID,
                result,
                String.format("Check %s", request.getUsername()));
    }

    public static Stream<PlayerRegistrationRequest> provideInvalidUsername() {
        return Stream.of(
                PlayerRegistrationRequest.builder().build(),
                PlayerRegistrationRequest.builder().username("").build(),
                PlayerRegistrationRequest.builder().username("user name").build(),
                PlayerRegistrationRequest.builder().username("user 123").build(),
                PlayerRegistrationRequest.builder().username("longusernamewithmorethan20characters").build(),
                PlayerRegistrationRequest.builder().username("user@123").build(),
                PlayerRegistrationRequest.builder().username("user#name").build(),
                PlayerRegistrationRequest.builder().username("user$name").build(),
                PlayerRegistrationRequest.builder().username("user%name").build(),
                PlayerRegistrationRequest.builder().username("user@example.com").build(),
                PlayerRegistrationRequest.builder().username("u").build(),
                PlayerRegistrationRequest.builder().username("us").build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPassword")
    public void testInvalidPassword(PlayerRegistrationRequest request) {
        PlayerRegistrationValidator.ValidationResult result = PlayerRegistrationValidator.isPasswordValid().apply(request);
        assertEquals(
                PlayerRegistrationValidator.ValidationResult.PASSWORD_NOT_VALID,
                result,
                String.format("Check %s", request.getPassword()));
    }

    public static Stream<PlayerRegistrationRequest> provideInvalidPassword() {
        return Stream.of(
                PlayerRegistrationRequest.builder().build(),
                PlayerRegistrationRequest.builder().password("").build(),
                PlayerRegistrationRequest.builder().password("Pwd1!").build(),
                PlayerRegistrationRequest.builder().password("ThisIsAReallyLongPassword123").build(),
                PlayerRegistrationRequest.builder().password("password1!").build(),
                PlayerRegistrationRequest.builder().password("PASSWORD1").build(),
                PlayerRegistrationRequest.builder().password("Password!").build(),
                PlayerRegistrationRequest.builder().password("Password123").build(),
                PlayerRegistrationRequest.builder().password("Pass word123!").build(),
                PlayerRegistrationRequest.builder().password("Password123").build(),
                PlayerRegistrationRequest.builder().password("Paaaaassword123").build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAvatar")
    public void testInvalidAvatar(PlayerRegistrationRequest request) {
        PlayerRegistrationValidator.ValidationResult result = PlayerRegistrationValidator.isAvatarValid().apply(request);
        assertEquals(
                PlayerRegistrationValidator.ValidationResult.AVATAR_NOT_VALID,
                result,
                String.format("Check %s", request.getAvatar()));
    }

    public static Stream<PlayerRegistrationRequest> provideInvalidAvatar() {
        return Stream.of(
                PlayerRegistrationRequest.builder().build(),
                PlayerRegistrationRequest.builder().avatar("").build(),
                PlayerRegistrationRequest.builder().avatar("ere").build(),
                PlayerRegistrationRequest.builder().avatar("ThisIsAReallyLongAvatar").build(),
                PlayerRegistrationRequest.builder().avatar("0").build(),
                PlayerRegistrationRequest.builder().avatar("avatar7").build(),
                PlayerRegistrationRequest.builder().avatar("avatar").build(),
                PlayerRegistrationRequest.builder().avatar("10").build()
        );
    }


    @Test
    public void testInvalidRePassword() {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .password("StrongP@ssw0rd")
                .repeatPassword("StrongP@ssw0r").build();
        PlayerRegistrationValidator.ValidationResult result = PlayerRegistrationValidator.isRepeatPasswordValid().apply(request);
        assertEquals(
                PlayerRegistrationValidator.ValidationResult.REPEAT_PASSWORD_NOT_VALID,
                result,
                String.format("Check %s", request.getAvatar()));
    }

    @ParameterizedTest
    @MethodSource("validRegistrationRequests")
    void testValidRegistrations(PlayerRegistrationRequest request) {
        PlayerRegistrationValidator.ValidationResult result = isUsernameValid()
                .and(isAvatarValid())
                .and(isPasswordValid())
                .and(isRepeatPasswordValid())
                .apply(request);
        assertEquals(SUCCESS, result);
    }

    static Stream<PlayerRegistrationRequest> validRegistrationRequests() {
        return Stream.of(
                PlayerRegistrationRequest.builder()
                        .username("validUser")
                        .password("StrongP@ssw0rd")
                        .repeatPassword("StrongP@ssw0rd")
                        .avatar("avatar1")
                        .build(),
                PlayerRegistrationRequest.builder()
                        .username("anotherUser")
                        .password("GoodP@ss1")
                        .repeatPassword("GoodP@ss1")
                        .avatar("avatar2")
                        .build(),
                PlayerRegistrationRequest.builder()
                        .username("testUser")
                        .password("P@ssw0rd123")
                        .repeatPassword("P@ssw0rd123")
                        .avatar("avatar3")
                        .build()
        );
    }

}