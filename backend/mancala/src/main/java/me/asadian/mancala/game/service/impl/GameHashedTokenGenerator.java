package me.asadian.mancala.game.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.game.repository.GameRepository;
import me.asadian.mancala.game.service.GameTokenGenerator;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;


@Component
@RequiredArgsConstructor
@Log4j2
public class GameHashedTokenGenerator implements GameTokenGenerator {

    private static final int TOKEN_LENGTH = 10;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final GameRepository repository;


    @Override
    public String generate() {
        StringBuilder token;

        do {
            token = new StringBuilder(TOKEN_LENGTH);
            for (int i = 0; i < TOKEN_LENGTH; i++) {
                int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
                token.append(ALLOWED_CHARACTERS.charAt(randomIndex));
            }
        } while (repository.findByToken(token.toString()).isPresent());

        return token.toString();
    }
}
