package me.asadian.mancala.player.service.impl;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.player.dto.AuthenticationRequest;
import me.asadian.mancala.player.repository.UserRepository;
import me.asadian.mancala.player.service.AuthenticationService;
import me.asadian.mancala.player.service.PlayerTransformer;
import me.asadian.mancala.shared.security.JwtService;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PlayerTransformer transformer;

    @Override
    public String getAuthToken(Player player) {
        return jwtService.generateToken(player.getUsername());
    }

    @Override
    public String authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        userRepository.findByUsername(request.getUsername()).orElseThrow();

        return jwtService.generateToken(request.getUsername());
    }

    @Override
    public Player getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetails)authentication.getPrincipal();
        var userModel = userRepository.findByUsername(user.getUsername()).orElseThrow();
        return transformer.transform(userModel);
    }
}
