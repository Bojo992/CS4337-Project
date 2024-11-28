package com.project.service;

import com.project.model.ChatMemberKey;
import com.project.model.ChatMembers;
import com.project.repository.ChatMembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatMembersService {

    @Autowired
    private ChatMembersRepository chatMemberRepository;

    public Optional<ChatMembers> getChatMemberById(ChatMemberKey id) {
        return chatMemberRepository.findById(id);
    }

    public void createChatMember(ChatMembers chatMember) {
        chatMemberRepository.save(chatMember);
    }

    public void updateChatMember(ChatMemberKey id, ChatMembers chatMember) {
        chatMemberRepository.findById(id).ifPresent(_ -> chatMemberRepository.save(chatMember));
    }

    public void deleteChatMember(ChatMemberKey id) {
        chatMemberRepository.deleteById(id);
    }
}
