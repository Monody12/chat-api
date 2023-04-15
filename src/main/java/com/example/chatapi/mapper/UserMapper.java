package com.example.chatapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chatapi.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
