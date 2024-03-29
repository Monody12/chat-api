package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatKey {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "`key`")
    private String key;
    /**
     * 剩余使用次数
     */
    private Integer times;
    private LocalDateTime createTime;
}
