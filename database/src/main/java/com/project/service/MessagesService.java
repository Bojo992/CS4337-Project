package com.project.service;

import com.project.model.Messages;
import com.project.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessagesService {

    @Autowired
    private MessagesRepository messageRepository;

    public Optional<Messages> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    public List<Messages> getMessagesByChat(long chatId) {
        return messageRepository.findByChatId(chatId);
    }

    public Messages createMessage(Messages message) {
        return messageRepository.save(message);
    }

    public void updateMessage(Long id, Messages message) {
        messageRepository.findById(id).ifPresent(_ -> messageRepository.save(message));
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}
