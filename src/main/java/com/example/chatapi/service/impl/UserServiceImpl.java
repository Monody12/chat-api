package com.example.chatapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.chatapi.exception.ExceptionEnum;
import com.example.chatapi.mapper.ChatKeyBindMapper;
import com.example.chatapi.mapper.UserMapper;
import com.example.chatapi.model.entity.User;
import com.example.chatapi.service.ChatKeyService;
import com.example.chatapi.service.UserService;
import com.example.chatapi.util.FormatChecker;
import com.example.chatapi.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.TimeUnit;

import static com.example.chatapi.constant.RedisConstant.RESET_PASSWORD_CODE_PREFIX;
import static com.example.chatapi.constant.RedisConstant.USER_TOKEN_PREFIX;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatKeyService chatKeyService;
    @Autowired
    private ChatKeyBindMapper chatKeyBindMapper;

    @Value("${chat.key.bind.max}")
    private Integer KEY_BIND_MAX;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String nickname, String password, String key) {
        // 检查用户名是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            ExceptionEnum.USERNAME_EXIST.throwException();
        }
        // 插入用户，回显id
        user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(password);
        userMapper.insert(user);
        // TODO 检查用户名昵称是否合规
        // 检查key是否存在
        chatKeyService.checkExist(key);
        // 检查key是否绑定数量超过最大值，在绑定key的时候检查
        // 绑定key
        chatKeyService.keyBindUser(key, user.getId());
    }

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            ExceptionEnum.USERNAME_NOT_EXIST.throwException();
        }
        if (!user.getPassword().equals(password)) {
            ExceptionEnum.PASSWORD_ERROR.throwException();
        }
        String token = UUIDUtils.getUUID();
        user.setToken(token);
        // 存入redis，有效期为7天
        redisTemplate.opsForValue().set(USER_TOKEN_PREFIX + token, user.getId().toString(), 7, TimeUnit.DAYS);
        return user;
    }

    @Override
    public void logout(String token, Integer userId) {
        String userIdStr = tokenToUserId(token);
        if (userIdStr != null && userId.equals(Integer.valueOf(userIdStr))) {
            redisTemplate.delete(getTokenRedisKey(token));
        }
    }

    /**
     * 更新用户信息
     * 修改昵称、密码、绑定的key
     *
     * @param user
     */
    @Override
    public int update(User user) {
        return userMapper.updateById(user);
    }

    private String getTokenRedisKey(String token) {
        return USER_TOKEN_PREFIX + token;
    }

    @Override
    public String tokenToUserId(String token) {
        String key = getTokenRedisKey(token);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 内部：根据用户名查询用户信息
     *
     * @param username
     */
    @Override
    public User selectByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer getUserId(String username, String email) {
        User user = selectByUsername(username);
        if (user == null) {
            ExceptionEnum.USERNAME_NOT_EXIST.throwException();
        } else if (!user.getMail().equals(email)) {
            ExceptionEnum.USERNAME_MAIL_NOT_MATCH.throwException();
        }
        return user.getId();
    }

    @Override
    public void resetPassword(Integer userId, String code, String password) {
        String key = RESET_PASSWORD_CODE_PREFIX + userId;
        String codeInRedis = redisTemplate.opsForValue().get(key);
        if (codeInRedis == null || !codeInRedis.equals(code)) {
            ExceptionEnum.VERIFICATION_CODE_ERROR.throwException();
        }
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        userMapper.updateById(user);
    }

    @Override
    public void changeNickname(String nickname, Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        userMapper.updateById(user);
    }

    @Override
    public void changeMail(String mail, Integer userId) {
        FormatChecker.mailCheck(mail);
        User user = new User();
        user.setId(userId);
        user.setMail(mail);
        userMapper.updateById(user);
    }
}
