package com.cs4337.project.util;

public class SQLQuery {
    public static final String getMaxIdForChat = "SELECT * FROM chat ORDER BY chat_id DESC LIMIT 1";
    public static final String findByChatIdForChatInfo = "SELECT * FROM chat_info WHERE chat_id IN :ids";
    public static final String existAllByIdForUser = "SELECT COUNT(DISTINCT id) = :len AS all_exist FROM users WHERE id IN :ids";
    public static final String saveForChatInfo = "insert into chat_info (chat_id, chat_name) values (?, ?)";
    public static final String saveForChat = "insert into chat (chat_id, user_id, is_admin) values (?, ?, ?)";
    public static final String deleteForChat = "DELETE FROM chat WHERE chat_id = ? and user_id = ?";
}