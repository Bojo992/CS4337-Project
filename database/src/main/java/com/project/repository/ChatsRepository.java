package com.project.repository;

import com.project.model.Chats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatsRepository extends JpaRepository<Chats, Long> {}
