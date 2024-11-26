package com.cs4337.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

/***
 * The Chat Message class. Modelled after the message in the database schema.
 *
 * @author royfl
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
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
     * The message type
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private MessageType type;
    /**
     * The timestamp of the message
     */
    private String sentAt;
    /**
     * Currently unused. URL of attached media.
     */
    private String media;
    /**
     * Currently unused. Indicator on whether message is seen.
     */
    private boolean isSeen;
    /**
     * Currently unused. URL of attached media.
     */
    private String room;
}
