package com.cs4337.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/***
 * Information about the chatroom. Includes the generated Id and a name.
 * for how chatrooms are connected to the user, see: {@link com.cs4337.project.model.Chat}
 * */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer chatId;
    private String chatName;
}
