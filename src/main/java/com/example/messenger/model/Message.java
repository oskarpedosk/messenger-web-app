package com.example.messenger.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private String content;
    private int senderId;
    private int recipientId;
    private MessageType type;
}
