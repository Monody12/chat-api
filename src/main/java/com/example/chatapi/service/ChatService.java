package com.example.chatapi.service;

import com.example.chatapi.model.entity.ChatHistory;
import com.example.chatapi.model.entity.ChatTopic;

import java.util.List;

public interface ChatService {

    ChatTopic newTopic(Integer userId, String topicName);

    List<ChatHistory> chat(Integer userid, List<ChatHistory> chatHistories);

    List<ChatHistory> getChatHistory(Integer userId,Integer topicId);

    List<ChatTopic> getChatTopic(Integer userId);

    void renameChatTopic(Integer userId,Integer topicId, String topic);

    void deleteChatTopic(Integer userId,Integer topicId);
}
