package com.cs4337.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The chatroom model. Contains a unique Id for chat,
 * a user id, and a bool to determine if the user is an admin.
 * for how chatrooms names and where the Id is generated, see: {@link com.cs4337.project.model.ChatInfo}
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    private Integer chatId;
    private Integer userId;
    private boolean isAdmin = false;
}
