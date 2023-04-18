package com.example.chatapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.chatapi.exception.ExceptionEnum;
import com.example.chatapi.mapper.ChatHistoryMapper;
import com.example.chatapi.mapper.ChatTopicMapper;
import com.example.chatapi.model.dto.ChatConversationDTO;
import com.example.chatapi.model.entity.ChatHistory;
import com.example.chatapi.model.entity.ChatTopic;
import com.example.chatapi.service.ChatHistoryService;
import com.example.chatapi.service.ChatKeyService;
import com.example.chatapi.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatHistoryMapper chatHistoryMapper;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatTopicMapper chatTopicMapper;
    @Autowired
    private ChatKeyService chatKeyService;
    @Value("${chat.py-server-url}")
    private String PY_SERVER_URL;

    @Autowired
    private ObjectMapper objectMapper;

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS))  // 设置连接池
            .build();

    @Override
    public ChatTopic newTopic(Integer userId, String topicName) {
        ChatTopic chatTopic = new ChatTopic();
        chatTopic.setUserId(userId);
        chatTopic.setTopic(topicName);
        chatTopicMapper.insert(chatTopic);
        return chatTopic;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ChatHistory> chat(Integer userId, List<ChatHistory> chatHistories) {
        // TODO 以后考虑使用redis分布式锁
        String lockKey = "user:" + userId;
        synchronized (lockKey.intern()) {
            // 处理次数
            chatKeyService.keyUseOnce(userId);
            // TODO 处理调用频率限制
            // TODO 处理聊天内容长度限制
            // TODO 处理聊天内容敏感词过滤
            // 调用接口 设置超时时间为30s
            String url = PY_SERVER_URL + "/chat";
            // 发送post请求，参数为content
            String content = getUrlContent(chatHistories);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Connection", "keep-alive")  // 添加 Connection 头
                    .post(okhttp3.RequestBody.create(content, okhttp3.MediaType.parse("application/json; charset=utf-8")))
                    .build();
            List<ChatConversationDTO> resConversationList = null;


            try (Response response = httpClient.newCall(request).execute()) {
                String res = response.body().string();
                try {
                    resConversationList = JSON.parseArray(res, ChatConversationDTO.class);
                } catch (JSONException e) {
                    try {
                        // 尝试将res转为Map<String, String>，如果转换成功则说明是AI API返回的请求错误信息
                        @SuppressWarnings("unchecked")
                        Map<String, LinkedHashMap<String,String>> map = objectMapper.readValue(res, Map.class);
                        LinkedHashMap<String, String> errorMap = map.get("error");
                        String code = errorMap.get("code");
                        if ("context_length_exceeded".equals(code)) {
                            String info = "对话长度超过限制，请清除一些对话框中不需要的内容！\n请注意：对话框中内容越多，生成耗费的时间越长！\n";
                            ExceptionEnum.CHAT_LENGTH_LIMIT.setMsg(info + res).throwException();
                        }
                    } catch (JsonProcessingException ex) {
                        ExceptionEnum.CHAT_REQUEST_ERROR.setMsg(e.getMessage()).throwException();
                    }
                }
                log.info("resConversationList: {}", res);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 转为ChatHistory
            Integer topicId = chatHistories.get(0).getTopicId();
            List<ChatHistory> resChatHistories = resConversationList.stream().map(chatConversationDTO -> {
                ChatHistory chatHistory = new ChatHistory();
                chatHistory.setUserId(userId);
                chatHistory.setTopicId(topicId);
                chatHistory.setRole(chatConversationDTO.getRole());
                chatHistory.setContent(chatConversationDTO.getContent());
                return chatHistory;
            }).collect(Collectors.toList());
            // 删除此话题下旧的聊天记录
            LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatHistory::getTopicId, topicId);
            queryWrapper.eq(ChatHistory::getUserId, userId);
            chatHistoryMapper.delete(queryWrapper);
            //  批量插入新的聊天记录
            chatHistoryService.saveBatch(resChatHistories);
            return resChatHistories;
        }
    }

    private String getCSRFTokenFromServer() {
        // 创建GET请求
        Request request = new Request.Builder()
                .url(PY_SERVER_URL + "/get_csrf_token")
                .build();
        String csrfToken = null;
        try (Response response = httpClient.newCall(request).execute()) {
            csrfToken = response.body().string();
            log.info("csrfToken: {}", csrfToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csrfToken;
    }

    private String getUrlContent(List<ChatHistory> chatHistories) {
        List<ChatConversationDTO> conversationList = chatHistories.stream().map(chatHistory -> {
            ChatConversationDTO chatConversationDTO = new ChatConversationDTO();
            chatConversationDTO.setRole(chatHistory.getRole());
            chatConversationDTO.setContent(chatHistory.getContent());
            return chatConversationDTO;
        }).toList();
        // 转为json
        return JSON.toJSONString(conversationList);
    }

    @Override
    public List<ChatHistory> getChatHistory(Integer userId, Integer topicId) {
        LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatHistory::getTopicId, topicId);
        queryWrapper.eq(ChatHistory::getUserId, userId);
        return chatHistoryMapper.selectList(queryWrapper);
    }

    @Override
    public List<ChatTopic> getChatTopic(Integer userId) {
        LambdaQueryWrapper<ChatTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatTopic::getUserId, userId);
        return chatTopicMapper.selectList(queryWrapper);
    }

    @Override
    public void renameChatTopic(Integer userId, Integer topicId, String topic) {
        LambdaQueryWrapper<ChatTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatTopic::getId, topicId);
        queryWrapper.eq(ChatTopic::getUserId, userId);
        ChatTopic chatTopic = chatTopicMapper.selectOne(queryWrapper);
        if (chatTopic == null) {
            return;
        }
        chatTopic.setTopic(topic);
        chatTopicMapper.updateById(chatTopic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChatTopic(Integer userId, Integer topicId) {
        // 删除聊天记录
        LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatHistory::getTopicId, topicId);
        queryWrapper.eq(ChatHistory::getUserId, userId);
        chatHistoryMapper.delete(queryWrapper);
        // 删除聊天主题
        LambdaQueryWrapper<ChatTopic> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ChatTopic::getId, topicId);
        queryWrapper1.eq(ChatTopic::getUserId, userId);
        chatTopicMapper.delete(queryWrapper1);
    }
}
