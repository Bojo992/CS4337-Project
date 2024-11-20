package com.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Contacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_one_id", nullable = false)
    private long userOneId;

    @Column(name = "user_two_id", nullable = false)
    private long userTwoId;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;
}
