package me.asadian.mancala.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String message) {
        // Broadcast the notification to all subscribers of the "/topic/notification" channel
        messagingTemplate.convertAndSend("/topic/notification", message);
    }
}