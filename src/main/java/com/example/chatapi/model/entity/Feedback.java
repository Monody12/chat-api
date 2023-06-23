package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    private String category;
    private String topic;
    private String content;
    private Integer importantRate;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
