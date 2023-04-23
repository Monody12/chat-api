package com.example.chatapi.service;

/**
 * 用户邮件服务
 */
public interface UserMailService {

    /**
     * 重设密码
     */
    void resetPassword(Integer userId, String ipAddr);

    /**
     * 换绑邮箱
     */
    void changeMail(Integer userId, String ipAddr);



}
