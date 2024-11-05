package com.cs4337.project.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;
    private LocalDateTime sentAt;
    private String media;
    private boolean isSeen;
}
