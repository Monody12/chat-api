package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ChatHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer topicId;
    private String role;
    private String content;
}
