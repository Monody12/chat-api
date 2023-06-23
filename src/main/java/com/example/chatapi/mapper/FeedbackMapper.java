package com.example.chatapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chatapi.model.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
