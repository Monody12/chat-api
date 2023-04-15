package com.example.chatapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.chatapi.mapper.ChatHistoryMapper;
import com.example.chatapi.model.entity.ChatHistory;
import com.example.chatapi.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ChatHistoryServerImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Override
    public boolean saveBatch(Collection<ChatHistory> entityList) {
        return super.saveBatch(entityList);
    }
}
