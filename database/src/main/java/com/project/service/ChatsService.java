package com.project.service;

import com.project.model.Chats;
import com.project.repository.ChatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatsService {

    @Autowired
    private ChatsRepository chatRepository;

    public Optional<Chats> getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public void createChat(Chats chat) {
        chatRepository.save(chat);
    }

    public void updateChat(Long id, Chats chat) {
        chatRepository.findById(id).ifPresent(_ -> chatRepository.save(chat));
    }

    public void deleteChat(Long id) {
        chatRepository.deleteById(id);
    }
}
