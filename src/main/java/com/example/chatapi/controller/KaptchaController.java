package com.example.chatapi.controller;

import com.example.chatapi.util.UUIDUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import static com.example.chatapi.constant.RedisConstant.VERIFICATION_CODE_PREFIX;

@Slf4j
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class KaptchaController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        String text = defaultKaptcha.createText();
        log.info("生成的验证码为：{}", text);

        // 生成一个uuid作为验证码的key，然后存入redis，有效期为5分钟
        String captchaKey = UUIDUtils.getUUID();
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX+ captchaKey, text.toLowerCase(), 5, TimeUnit.MINUTES);
        // 将验证码的key放入响应头中，并设置允许跨域请求的头信息
        response.setHeader("Captcha-Key", captchaKey);
        response.setHeader("Access-Control-Expose-Headers", "Captcha-Key");

        // 生成验证码图像
        BufferedImage image = defaultKaptcha.createImage(text);
        ImageIO.write(image, "jpeg", response.getOutputStream());


    }
}
