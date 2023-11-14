package me.asadian.mancala.game.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationController {


    @MessageMapping("/sendNotification")
    @SendTo("/topic/notification")
    public String sendNotification(String message) {
        return message;
    }
}