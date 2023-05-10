package com.example.chatapi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// TODO 以后需要鉴权再打开
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Autowired
    private VerifyCodeInterceptor verifyCodeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录状态校验
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/key/**", "/chat/**")
                .addPathPatterns("/user/nickname","/user/mail");
//                .excludePathPatterns("/user/login", "/user/register", "/user/logout", "/user/not_login")
//                .excludePathPatterns("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg",
//                        "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.woff","/**/*.map", "/**/*.ttf", "/**/*.woff2")
//                .excludePathPatterns("/error");
        // 验证码校验
        registry.addInterceptor(verifyCodeInterceptor).addPathPatterns("/**");
    }

}
