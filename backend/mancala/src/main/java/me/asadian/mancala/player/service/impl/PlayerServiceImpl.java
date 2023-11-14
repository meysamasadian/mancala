package me.asadian.mancala.player.service.impl;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import me.asadian.mancala.player.exceptions.PlayerRegistrationInvalidDataException;
import me.asadian.mancala.shared.security.Role;
import me.asadian.mancala.player.model.UserModel;
import me.asadian.mancala.player.repository.UserRepository;
import me.asadian.mancala.player.service.PlayerRegistrationValidator;
import me.asadian.mancala.player.service.PlayerService;
import me.asadian.mancala.player.service.PlayerTransformer;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static me.asadian.mancala.player.service.PlayerRegistrationValidator.*;
import static me.asadian.mancala.player.service.PlayerRegistrationValidator.ValidationResult.SUCCESS;
import static me.asadian.mancala.player.service.PlayerRegistrationValidator.ValidationResult.USERNAME_IS_DUPLICATED;

@Component
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final UserRepository repository;
    private final PlayerTransformer transformer;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<Player> getPlayer(String username) {
        return repository.findByUsername(username).map(transformer::transform);
    }

    @Override
    public Player register(PlayerRegistrationRequest request) throws PlayerRegistrationInvalidDataException {
        ValidationResult result = isUsernameValid()
                .and(isAvatarValid())
                .and(isPasswordValid())
                .and(isRepeatPasswordValid())
                .and(isUsernameDuplicated())
                .apply(request);

        if (!SUCCESS.equals(result)) {
            throw new PlayerRegistrationInvalidDataException(result.getMessage());
        }

        UserModel playerModel =repository.save(UserModel.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .avatar(request.getAvatar())
                        .role(Role.GAMER)
                .build());

        return transformer.transform(playerModel);
    }

    private PlayerRegistrationValidator isUsernameDuplicated() {
        return request -> repository.findByUsername(request.getUsername()).isEmpty() ? SUCCESS : USERNAME_IS_DUPLICATED;
    }

}
