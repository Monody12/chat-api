package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ChatKeyBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer keyId;
    private Integer userId;
}
