package com.cs4337.project.repository;

import com.cs4337.project.model.Chat;
import com.cs4337.project.util.SQLQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    public Optional<List<Chat>> findAllByUserId(int id);
    public boolean existsByChatIdAndUserId(int id, int userId);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = SQLQuery.deleteForChat)
    public void deleteByChatIdAndUserIdQuery(int id, int userId);
    @Query(nativeQuery = true, value = SQLQuery.getMaxIdForChat)
    public Optional<Chat> findTopByChatIdQuery();
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = SQLQuery.saveForChat)
    public void saveChat(int chatId, int userId, boolean isAdmin);
}
