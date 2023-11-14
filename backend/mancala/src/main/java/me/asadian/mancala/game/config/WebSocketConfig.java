package me.asadian.mancala.game.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static me.asadian.mancala.game.ws.constants.SocketAddress.*;
import static me.asadian.mancala.shared.security.Role.GAMER;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(GAME_SECURED_MAKE_MOVE).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(GAME_SECURED_QUEUE, GAME_PLAYER_SECURED_QUEUE, GAME_PLAYER_ERROR_SECURED_QUEUE);
        registry.setApplicationDestinationPrefixes("/mancala-game");
        registry.setUserDestinationPrefix("/secured/player");
    }

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpDestMatchers("/**").hasRole(GAMER.name())
                .anyMessage().authenticated();
        return messages.build();
    }
}
