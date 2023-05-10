package com.example.chatapi.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.Random;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", getRandomColor());
        properties.setProperty("kaptcha.background.clear.from", getRandomColor());
        properties.setProperty("kaptcha.background.clear.to", getRandomColor());
        properties.setProperty("kaptcha.noise.color", getRandomColor());
        properties.setProperty("kaptcha.border.color", getRandomColor());
        properties.setProperty("kaptcha.textproducer.char.space", String.valueOf(new Random().nextInt(5) + 5)); // 随机字符间距
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        properties.setProperty("kaptcha.session.key", "kaptchaCode");
        // 设置验证码出现的字符为：数字和大小写字母
        properties.setProperty("kaptcha.textproducer.char.string",
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

    // 随机生成颜色
    private String getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return r + "," + g + "," + b;
    }

}
