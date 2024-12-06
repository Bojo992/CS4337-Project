package com.project.security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    private Integer id;
    private String email;
    private String username;
    private String password;
    private String profilePicture;
    private String statusMessage;
    private String roles;
}
