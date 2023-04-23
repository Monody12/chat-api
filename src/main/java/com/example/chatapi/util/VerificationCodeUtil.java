package com.example.chatapi.util;

import java.util.Random;

public class VerificationCodeUtil {
    /**
     * 生成6位数字验证码
     * @return 验证码字符串
     */
    public static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
