package com.cs4337.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 * The body for creating a group chat. For one-on-one chats, see: {@link com.cs4337.project.model.CreatPersonalChatRequest}
 * Takes a title, and a list of userIds.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatGroupChatRequest {
    private String title;
    private List<Integer> userIds;
}
