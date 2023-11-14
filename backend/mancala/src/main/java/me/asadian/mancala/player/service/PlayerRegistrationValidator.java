package me.asadian.mancala.player.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import java.util.function.Function;
import java.util.regex.Pattern;

import static me.asadian.mancala.player.service.PlayerRegistrationValidator.ValidationResult.*;

public interface PlayerRegistrationValidator extends Function<PlayerRegistrationRequest, PlayerRegistrationValidator.ValidationResult> {

    Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!*])(?!.*\\s).{8,20}$");
    Pattern AVATAR_PATTERN = Pattern.compile("^avatar[1-6]$");

    /**
     * allows usernames that consist of alphanumeric characters and underscores, with a length between 3 and 20 characters.
     * @return PlayerRegistrationValidator
     */
    static PlayerRegistrationValidator isUsernameValid() {
        return request -> request.getUsername() != null
                && !request.getUsername().isBlank()
                && USERNAME_PATTERN.matcher(request.getUsername()).matches() ? SUCCESS : USERNAME_NOT_VALID;
    }

    /**
     * allows avatar that consist of number between avatar1-avatar6.
     * @return PlayerRegistrationValidator
     */
    static PlayerRegistrationValidator isAvatarValid() {
        return request -> request.getAvatar() != null
                && !request.getAvatar().isBlank()
                && AVATAR_PATTERN.matcher(request.getAvatar()).matches() ? SUCCESS : AVATAR_NOT_VALID;
    }

    /**
     * At least 8 characters and at most 20 characters in length.
     * At least one uppercase letter (A-Z).
     * At least one lowercase letter (a-z).
     * At least one digit (0-9).
     * At least one special character from the set [@#$%^&+=!*].
     * No whitespace characters (spaces) are allowed.
     * @return PlayerRegistrationValidator
     */
    static PlayerRegistrationValidator isPasswordValid() {
        return request -> request.getPassword() != null
                && !request.getPassword().isBlank()
                && PASSWORD_PATTERN.matcher(request.getPassword()).matches()? SUCCESS : PASSWORD_NOT_VALID;
    }

    /**
     * Check the password and re-password are alike
     * @return PlayerRegistrationValidator
     */
    static PlayerRegistrationValidator isRepeatPasswordValid() {
        return request -> request.getPassword().equals(request.getRepeatPassword()) ? SUCCESS : REPEAT_PASSWORD_NOT_VALID;
    }

    default PlayerRegistrationValidator and(PlayerRegistrationValidator other) {
        return request -> {
            ValidationResult result = this.apply(request);
            return result.equals(SUCCESS) ? other.apply(request) : result;
        };
    }


    @Getter
    @AllArgsConstructor
    enum ValidationResult {
        SUCCESS(null),
        USERNAME_NOT_VALID("username is not valid"),
        AVATAR_NOT_VALID("avatar is not valid"),
        PASSWORD_NOT_VALID("password is not valid or strong enough"),
        REPEAT_PASSWORD_NOT_VALID("Re-password is not match with password"),
        USERNAME_IS_DUPLICATED("username is duplicated");

        private final String message;

    }

}
