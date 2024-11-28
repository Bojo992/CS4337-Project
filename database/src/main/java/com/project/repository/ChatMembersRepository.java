package com.project.repository;

import com.project.model.ChatMemberKey;
import com.project.model.ChatMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMembersRepository extends JpaRepository<ChatMembers, ChatMemberKey> {

}
