package com.cs4337.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatPersonalChatRequest {
    private String title;
    private int userId1;
    private int userId2;
}
