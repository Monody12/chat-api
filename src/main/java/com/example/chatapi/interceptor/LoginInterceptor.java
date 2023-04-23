package com.example.chatapi.interceptor;

import com.example.chatapi.exception.ExceptionEnum;
import com.example.chatapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 在请求到达Controller控制器之前 通过拦截器执行一段代码
     * 如果方法返回true,继续执行后续操作
     * 如果返回false，执行中断请求处理，请求不会发送到Controller
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("HttpServletRequest class:{}",request.getClass().getName());
        if ("OPTIONS".equals(request.getMethod())) { // 检查请求是否为CORS预检请求
            response.setStatus(HttpServletResponse.SC_OK);
            return true; // 不拦截CORS预检请求
        }
        // 打印请求路径
        log.info("请求路径：" + request.getRequestURI());
        // 获取ParameterMap
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((key, value) -> log.info("请求参数：{}={}", key, value[0]));
        // 获取请求头中的token和userId
        String Authorization = request.getHeader("Authorization");
        if (Authorization == null) {
            ExceptionEnum.MISSING_REQUEST_HEADER.throwException();
            return false;
        }
        String[] authorization = Authorization.split("#");
        if (authorization.length != 2) {
            log.info("请求头中的token和userId为空");
            // 跳转到未登录的json信息
            request.getRequestDispatcher("/user/not_login").forward(request, response);
            return false;
        }
        String token = authorization[0], headerUserId = authorization[1];
        // 如果token为空，或者不存在于redis中，返回false
        String userId = userService.tokenToUserId(token);
        if (token == null || userId == null || !userId.equals(headerUserId)) {
            log.info("token为空或者不存在于redis中");
            log.info("token:{} headerUID:{} userId:{}", token, headerUserId, userId);
            // 跳转到未登录的json信息
            request.getRequestDispatcher("/user/not_login").forward(request, response);
            return false;
        }
        // 写回信息到request中
        request.setAttribute("userId", userId);
        request.setAttribute("token", token);
        return super.preHandle(request, response, handler);
    }

    /**
     * 控制器之后，跳转前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 跳转之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
