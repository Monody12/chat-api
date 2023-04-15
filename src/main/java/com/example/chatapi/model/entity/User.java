package com.example.chatapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    /**
     * 绑定的key
     */
    @TableField(value = "`key`")
    private String key;
    /**
     * 用户登录token，不存在数据表，存在redis中
     */
    @TableField(exist = false)
    private String token;

}
