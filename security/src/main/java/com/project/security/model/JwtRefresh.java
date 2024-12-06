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
public class JwtRefresh {
    private Integer id;
    private String refreshToken;
    private String currentToken;
}
