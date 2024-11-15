package com.cs4337.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * A model that adds or removes a user from a group chat depending on if the user is already in the chat with that ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddOrRemoveUserFromGroupChatRequest {
    private int chatId;
    private int userId;
}
