package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatKeyBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer keyId;
    private Integer userId;
    private LocalDateTime createTime;
}
