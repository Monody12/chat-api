package com.example.chatapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chatapi.model.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
