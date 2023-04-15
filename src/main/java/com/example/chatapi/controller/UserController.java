package com.example.chatapi.controller;

import com.example.chatapi.model.entity.User;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import com.example.chatapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(String username,String password,String nickname,String key){
        userService.register(username,nickname,password,key);
        return ResultEnum.SUCCESS.getResult();
    }

    @PostMapping("/login")
    public Result login(String username,String password){
        User user = userService.login(username, password);
        return ResultEnum.SUCCESS.setData(user).getResult();
    }

    @GetMapping("/logout")
    public Result logout(@RequestHeader String token,@RequestHeader Integer userId){
        userService.logout(token,userId);
        return ResultEnum.SUCCESS.getResult();
    }

    @GetMapping("/not_login")
    public Result notLogin(){
        return ResultEnum.UNAUTHORIZED.getResult();
    }

}
