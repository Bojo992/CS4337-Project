package com.cs4337.project.service;

import com.cs4337.project.model.*;
import com.cs4337.project.repository.ChatInfoRepository;
import com.cs4337.project.repository.ChatRepository;
import com.cs4337.project.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/***
 * The service class for managing users and chats. Manages functions more difficult to achieve with websockets, such as group chat creation and allocation.
 * Controlled by: {@link com.cs4337.project.controller.ChatRESTController}
 */
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatInfoRepository chatInfoRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, ChatInfoRepository chatInfoRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.chatInfoRepository = chatInfoRepository;
        this.userRepository = userRepository;
    }

    /***
     * Gets all info about chat's that user is part of
     *
     * @param userId user's id
     * @return returns list of ChatInfo if user is a part of chats or a single chat. In case user don't have any chat's or userId was incorrect it should return HttpStatus.NOT_FOUND and relevant message
     *
     */
    public Map<String, Object> getAllChatsForUser(Integer userId) {
        Optional<List<Chat>> userChatIds = null;

        try {
            //getting all chats that user is part of
            userChatIds = chatRepository.findAllByUserId(userId);
        } catch (Exception e) {
            //error return
            return Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND);
        } finally {
            //check if the list of chats aren't empty
            if (userChatIds.isPresent() && userChatIds.get().size() > 0) {
                List<Integer> chatIds = userChatIds.get().stream()
                        .map(i -> (i.getChatId()))
                        .collect(Collectors.toList());
                var chatInfoList = chatInfoRepository.findByChatId(chatIds);

                //return info about all chat's
                if (chatInfoList.isPresent()) {
                    return Map.of("chats", chatInfoList.get(), "status", HttpStatus.OK);
                }
            }
        }

        //in case list was empty
        return Map.of("message", "user isn't a part of any chat", "status", HttpStatus.NOT_FOUND);
    }

    public Map<String, Object> createPersonalChat(CreatPersonalChatRequest request) {
        Optional<Chat> maxChat = null;

        if (!(userRepository.existsById(request.getUserId1()) && userRepository.existsById(request.getUserId2()))) {
            return Map.of("status", HttpStatus.CONFLICT, "message", "one of the users doesn't exist");
        }


        //get last max id of chat
        try {
            maxChat = chatRepository.findTopByChatIdQuery();
        } catch (Exception e) {
            System.out.println("this might be first chat");
        }

        //check if this is first chat
        Integer newId = (maxChat != null && maxChat.isPresent()) ? maxChat.get().getChatId() + 1 : 1;

        //create 2 chat entries and chat info
        Chat chatForUser1 = Chat.builder().isAdmin(true).userId(request.getUserId1()).chatId(newId).build();
        Chat chatForUser2 = Chat.builder().isAdmin(true).userId(request.getUserId2()).chatId(newId).build();
        ChatInfo chatInfo = ChatInfo.builder().chatName(request.getTitle()).chatId(newId).build();


        //try to save it
        try {
            chatInfoRepository.saveChatInfo(chatInfo.getChatId(),chatInfo.getChatName());
            chatRepository.saveChat(chatForUser1.getChatId(),chatForUser1.getUserId(), chatForUser1.isAdmin());
            chatRepository.saveChat(chatForUser2.getChatId(),chatForUser2.getUserId(), chatForUser2.isAdmin());
        } catch (Exception e) {
            return Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND, "message", "bojo");
        }

        return Map.of("message", "chat created successfully", "status", HttpStatus.OK);
    }

    public Map<String, Object> creatGroupChat(CreatGroupChatRequest request) {
        Optional<Chat> maxChat = chatRepository.findTopByChatIdQuery();

        //check if all users exist
        if (userRepository.existsAllById(request.getUserIds(), request.getUserIds().size()) == 0) {
            return Map.of("status", HttpStatus.CONFLICT, "message", "one of the users doesn't exist");
        }

        //get last max id of chat
        try {
            maxChat = chatRepository.findTopByChatIdQuery();
        } catch (Exception e) {
            System.out.println("this might be first chat");
        }

        Integer newId = (maxChat != null && maxChat.isPresent()) ? maxChat.get().getChatId() + 1 : 1;

        List<Chat> chatList = new ArrayList<>();

        for (Integer userId : request.getUserIds()) {
            chatList.add(Chat.builder().isAdmin(true).userId(userId).chatId(newId).build());
        }

        ChatInfo chatInfo = ChatInfo.builder().chatName(request.getTitle()).chatId(newId).build();

        try {
            for (var chat : chatList) {
                chatRepository.saveChat(chat.getChatId(),chat.getUserId(), chat.isAdmin());
            }

            chatInfoRepository.saveChatInfo(chatInfo.getChatId(),chatInfo.getChatName());
        } catch (Exception e) {
            return Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND);
        }

        return Map.of("message", "chat created successfully", "status", HttpStatus.OK);
    }

    public Map<String, Object> removeUserFromGroupChat(AddOrRemoveUserFromGroupChatRequest request) {
        if (!userRepository.existsById(request.getUserId()) || !chatRepository.existsByChatIdAndUserId(request.getChatId(), request.getUserId())) {
            return Map.of("status", HttpStatus.CONFLICT, "message", "user doesn't exist or isn't a part of the chat");
        }

        try {
            chatRepository.deleteByChatIdAndUserIdQuery(request.getChatId(), request.getUserId());
        } catch (Exception e) {
            return Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND);
        }

        return Map.of("message", "user removed successfully", "status", HttpStatus.OK);
    }

    public Map<String, Object> addUserToGroupChat(AddOrRemoveUserFromGroupChatRequest request) {
        if (!userRepository.existsById(request.getUserId()) || chatRepository.existsByChatIdAndUserId(request.getChatId(), request.getUserId())) {
            return Map.of("status", HttpStatus.CONFLICT, "message", "user doesn't exist or a part of the chat");
        }

        Chat chat = Chat.builder().isAdmin(false).userId(request.getUserId()).chatId(request.getChatId()).build();

        try {
            chatRepository.saveChat(chat.getChatId(), chat.getUserId(), chat.isAdmin());
        } catch (Exception e) {
            return Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND);
        }

        return Map.of("message", "user add successfully", "status", HttpStatus.OK);
    }
}
