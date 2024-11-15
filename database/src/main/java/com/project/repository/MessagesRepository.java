package com.project.repository;

import com.project.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages, Long> {
    List<Messages> findByChatId(long chat_id);
}
