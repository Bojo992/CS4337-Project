package com.cs4337.project.repository;

import com.cs4337.project.model.ChatInfo;
import com.cs4337.project.util.SQLQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatInfoRepository extends JpaRepository<ChatInfo, Integer> {
    @Query(nativeQuery = true, value = SQLQuery.findByChatIdForChatInfo)
    public Optional<List<ChatInfo>> findByChatId(@Param("ids") List<Integer> ids);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = SQLQuery.saveForChatInfo)
    public void saveChatInfo(int chatId, String chatName);
}
