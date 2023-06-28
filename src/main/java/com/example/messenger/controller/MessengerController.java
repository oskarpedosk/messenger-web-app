package com.example.messenger.controller;

import com.example.messenger.model.MessengerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class MessengerController {

    @SendTo("/topic/public")
    @MessageMapping("/messenger.sendMessage")
    public MessengerMessage sendMessage(
            @Payload MessengerMessage messengerMessage
    ) {
        return messengerMessage;
    }

    @SendTo("/topic/public")
    @MessageMapping("/messenger.addUser")
    public MessengerMessage addUser(
            @Payload MessengerMessage messengerMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in websocket session
        headerAccessor.getSessionAttributes().put("username", messengerMessage.getSender());
        return messengerMessage;
    }
}
