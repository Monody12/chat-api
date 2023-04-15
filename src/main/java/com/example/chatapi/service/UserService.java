package com.example.chatapi.service;

import com.example.chatapi.model.entity.User;

public interface UserService {

    void register(String username,String nickname, String password,String key);

    User login(String username, String password);

    void logout(String token,Integer userId);

    /**
     * 更新用户信息
     * 修改昵称、密码、绑定的key
     */
    int update(User user);

    String tokenToUserId(String token);

}
