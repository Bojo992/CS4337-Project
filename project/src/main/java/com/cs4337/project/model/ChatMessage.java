package com.cs4337.project.model;

import lombok.*;

import java.time.LocalDateTime;

/***
 * The Chat Message class. Modelled after the message in the database schema.
 *
 * @author royfl
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    /**
     * The body of the message
     */
    private String content;
    /**
     * The username of the sender
     */
    private String sender;
    /**
     * The type of message, usually 'CHAT'
     */
    private MessageType type;
    /**
     * The timestamp of the message
     */
    private LocalDateTime sentAt;
    /**
     * Currently unused. URL of attached media.
     */
    private String media;
    /**
     * Currently unused. Indicator on whether message is seen.
     */
    private boolean isSeen;
}
