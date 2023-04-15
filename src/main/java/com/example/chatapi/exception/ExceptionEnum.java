package com.example.chatapi.exception;

import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;

/**
 * 异常枚举类
 * 1000-1099 为用户异常
 * 1100-1199 为key异常
 * 1200-1299 为聊天异常
 * 2000- 为通用异常
 */
public enum ExceptionEnum {
    USERNAME_EXIST(1001, "用户名已存在"),
    USERNAME_NOT_EXIST(1002, "用户名不存在"),
    PASSWORD_ERROR(1003, "用户名或密码错误"),

    // key绑定用户数到达上限
    KEY_USER_LIMIT(1101, "key绑定用户数到达上限"),
    // key不存在
    KEY_NOT_EXIST(1102, "key不存在"),
    // key使用次数不足
    KEY_USE_LIMIT(1103, "key使用次数不足"),
    // 对话长度超过限制
    CHAT_LENGTH_LIMIT(1201, "对话长度超过限制"),
    // 对话请求异常
    CHAT_REQUEST_ERROR(1202, "对话请求异常"),
    // 缺少请求头参数
    MISSING_REQUEST_HEADER(2001, "缺少请求头参数"),
    // 缺少请求参数
    MISSING_REQUEST_PARAM(2002, "缺少请求参数"),
    // 非法参数
    ILLEGAL_ARGUMENT(2003, "非法参数");


    private Integer code;
    private String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionEnum setMsg(String message) {
        this.message = message;
        return this;
    }

    public RuntimeException getException() {
        return new ExceptionModel(code, message);
    }

    public void throwException() {
        throw getException();
    }

    public Result getResult() {
        return ResultEnum.ERROR.setCode(code).setMsg(message).getResult();
    }
}
