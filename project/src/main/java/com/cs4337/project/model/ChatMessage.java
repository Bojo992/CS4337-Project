package com.cs4337.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    @JsonIgnore
    private String sender;
    private MessageType type;

    @JsonIgnore
    private LocalDateTime sentAt;

    private String media;
    private boolean isSeen;
}