package com.example.messenger.chat;

import com.example.messenger.message.Action;
import com.example.messenger.message.Message;
import com.example.messenger.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    HashMap<String, String> clients = new HashMap<String, String>();

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message message) {
        String sessionId = clients.get(message.getRecipient());

        // TODO Add message to database

        messagingTemplate.convertAndSend("/private" + "-user" + sessionId, message);
    }

    @MessageMapping("/chat.login")
    public void login(@Payload User user, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        clients.put(user.getUsername(), headerAccessor.getSessionId());
        log.info("User logged in: {}", user.getUsername());
        var message = Message.builder()
                .action(Action.LOGIN)
                .sender(user.getUsername())
                .build();
        messagingTemplate.convertAndSend("/all/notifications", message);
    }
}