package com.example.chatapi.interceptor;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/user/logout", "/user/not_login")
                .excludePathPatterns("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg",
                        "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.woff","/**/*.map", "/**/*.ttf", "/**/*.woff2")
                .excludePathPatterns("/error");
    }

}
