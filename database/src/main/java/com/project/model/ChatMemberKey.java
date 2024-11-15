package com.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChatMemberKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "chat_id", nullable = false)
    private int chatId;

}