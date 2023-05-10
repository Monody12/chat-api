package com.example.chatapi.controller;

import com.example.chatapi.captcha.VerifyCode;
import com.example.chatapi.mapper.UserMapper;
import com.example.chatapi.model.entity.User;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import com.example.chatapi.service.UserMailService;
import com.example.chatapi.service.UserService;
import com.example.chatapi.util.IpUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMailService userMailService;

    @VerifyCode
    @PostMapping("/register")
    public Result register(@NotNull String username,@NotNull String password,
                           @NotNull String nickname,@NotNull String key){
        userService.register(username,nickname,password,key);
        return ResultEnum.SUCCESS.getResult();
    }

    @VerifyCode
    @PostMapping("/login")
    @CrossOrigin(origins = "*",maxAge = 3600)
    public Result login(@NotNull String username,@NotNull String password){
        User user = userService.login(username, password);
        return ResultEnum.SUCCESS.setData(user).getResult();
    }

    @GetMapping("/logout")
    public Result logout(@RequestAttribute String token,@RequestAttribute Integer userId){
        userService.logout(token,userId);
        return ResultEnum.SUCCESS.getResult();
    }

    @GetMapping("/not_login")
    public Result notLogin(HttpServletResponse response){
        // 设置状态码为401
        response.setStatus(401);
        return ResultEnum.UNAUTHORIZED.getResult();
    }

    /**
     * 密码重置：输入用户名和邮箱，获取用户id
     */
    @GetMapping("/id")
    public Result getUserId(String username,String mail){
        Integer userId = userService.getUserId(username, mail);
        return ResultEnum.SUCCESS.setData(userId).getResult();
    }

    /**
     * 密码重置：获取验证码
     */
    @GetMapping("/reset/password")
    public Result resetPassword(@NotNull Integer userId, HttpServletRequest request){
        String ipAddr = IpUtils.getClientIpAddress(request);
        userMailService.resetPassword(userId,ipAddr);
        return ResultEnum.SUCCESS.getResult();
    }

    /**
     * 密码重置，提交验证码和新密码
     */
    @VerifyCode
    @PostMapping("/reset/password")
    public Result resetPassword(@NotNull Integer userId,@NotNull String code,@NotNull String password){
        userService.resetPassword(userId,code,password);
        return ResultEnum.SUCCESS.getResult();
    }

    /**
     * 修改昵称
     * 需要鉴权，userId从请求头中获取，请求路径添加到拦截器中
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @PostMapping("/nickname")
    public Result changeNickname(@NotNull String nickname,@RequestAttribute Integer userId){
        userService.changeNickname(nickname,userId);
        return ResultEnum.SUCCESS.getResult();
    }

    /**
     * 修改邮箱
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @PostMapping("/mail")
    public Result changeMail(@NotNull String mail,@RequestAttribute Integer userId){
        userService.changeMail(mail,userId);
        return ResultEnum.SUCCESS.getResult();
    }
}
