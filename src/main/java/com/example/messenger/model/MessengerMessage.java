package com.example.messenger.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessengerMessage {
    private String content;
    private String sender;
    private String recipient;
    private MessageType type;
}
