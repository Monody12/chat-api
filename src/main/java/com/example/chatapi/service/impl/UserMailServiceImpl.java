package com.example.chatapi.service.impl;

import com.example.chatapi.exception.ExceptionEnum;
import com.example.chatapi.mapper.UserMapper;
import com.example.chatapi.model.entity.User;
import com.example.chatapi.service.UserMailService;
import com.example.chatapi.service.UserService;
import com.example.chatapi.util.FormatChecker;
import com.example.chatapi.util.SendEmail;
import com.example.chatapi.util.VerificationCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.chatapi.constant.RedisConstant.RESET_PASSWORD_CODE_PREFIX;

/**
 * 用户邮件服务
 */
@Service
public class UserMailServiceImpl implements UserMailService {

    @Value("${mail.verification-code.mailbox-limit}")
    private Integer MAILBOX_LIMIT;
    @Value("${mail.verification-code.ip-limit}")
    private Integer IP_LIMIT;

    @Autowired
    private SendEmail sendEmail;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    /**
     * 发送邮件前检查
     * @param ipAddr
     * @param mail
     */
    private void preSendCheck(String ipAddr, String mail) {
        FormatChecker.mailCheck(mail);
        checkLimit(ipAddr, mail);
    }

    /**
     * 发送一封邮件前，检查是否超过限制
     */
    private void checkLimit(String ipAddr, String mail) {
        // 检查邮箱是否超过限制
        String emailKey = "mail:" + mail;
        Long count = redisTemplate.opsForValue().increment(emailKey, 1);
        if (count == 1) {
            redisTemplate.expire(emailKey, 1, TimeUnit.HOURS);
        }else if (count > MAILBOX_LIMIT) {
            ExceptionEnum.MAIL_SEND_LIMIT.throwException();
        }
        // 检查ip是否超过限制
        String ipKey = "ip:" + ipAddr;
        count = redisTemplate.opsForValue().increment(ipKey, 1);
        if (count == 1) {
            redisTemplate.expire(ipKey, 1, TimeUnit.HOURS);
        }else if (count > IP_LIMIT) {
            ExceptionEnum.IP_SEND_LIMIT.throwException();
        }
    }

    /**
     * 重设密码
     *
     * @param userId
     * @param ipAddr
     */
    @Override
    public void resetPassword(Integer userId, String ipAddr) {
        User user = userMapper.selectById(userId);
        String mail = user.getMail();
        String nickname = user.getNickname();
        preSendCheck(ipAddr, mail);
        String code = VerificationCodeUtil.generateVerificationCode();
        sendEmail.sendVerificationCode(mail, "密码重置", nickname, Integer.parseInt(code));
        // 存储验证码到redis
        String key = RESET_PASSWORD_CODE_PREFIX + userId;
        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
    }

    /**
     * 换绑邮箱
     *
     * @param userId
     * @param ipAddr
     */
    @Override
    public void changeMail(Integer userId, String ipAddr) {

    }
}
