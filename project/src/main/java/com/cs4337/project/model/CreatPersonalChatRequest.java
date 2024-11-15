package com.cs4337.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * The body for creating a one-on-one chat. For chats with more than 2 users, see: {@link com.cs4337.project.model.CreatGroupChatRequest}
 * This is seperate as personal chats usually have different rules to group chats.
 * Takes a title, and two userIds.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatPersonalChatRequest {
    private String title;
    private int userId1;
    private int userId2;
}
