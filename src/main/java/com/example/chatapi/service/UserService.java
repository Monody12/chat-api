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

    /**
     * 内部：根据用户名查询用户信息
     */
    User selectByUsername(String username);

    Integer getUserId(String username, String email);

    void resetPassword(Integer userId, String code, String password);

    void changeNickname(String nickname, Integer userId);

    void changeMail(String mail, Integer userId);
}
