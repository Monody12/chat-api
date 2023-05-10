package com.example.chatapi.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.chatapi.captcha.VerifyCode;
import com.example.chatapi.exception.ExceptionEnum;
import com.google.code.kaptcha.Constants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

import static com.example.chatapi.constant.RedisConstant.VERIFICATION_CODE_PREFIX;

@Slf4j
@Component
public class VerifyCodeInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(VerifyCode.class)) {
                //  从请求头中获取验证码的key和用户输入的验证码
                String captchaKey = request.getHeader("captchaKey");
                String captcha = request.getHeader("captcha");
                // 不区分大小写，服务端存储的验证码都是小写
                if (captcha != null) {
                    captcha = captcha.toLowerCase();
                }
                // 从redis中获取验证码
                String redisCaptcha = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + captchaKey);
                // 判断验证码是否正确
                if (StringUtils.isBlank(redisCaptcha) || !redisCaptcha.equals(captcha)) {
                    log.info("验证码错误");
                    throw ExceptionEnum.VERIFICATION_CODE_ERROR.getException();
                }
                // 验证码正确，删除redis中的验证码
                else {
                    redisTemplate.delete(VERIFICATION_CODE_PREFIX + captchaKey);
                }
            }
        }
        return true;
    }

}