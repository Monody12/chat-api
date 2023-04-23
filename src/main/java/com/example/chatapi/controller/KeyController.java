package com.example.chatapi.controller;

import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import com.example.chatapi.service.ChatKeyService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/key")
@CrossOrigin(origins = "*", maxAge = 3600)
public class KeyController {

    @Autowired
    private ChatKeyService chatKeyService;

    @GetMapping("/times")
    public Result getTimes(@RequestAttribute Integer userId) {
        int times = chatKeyService.keyTimes(userId);
        return ResultEnum.SUCCESS.setData(times).getResult();
    }

    @PostMapping("/changeBind")
    public Result changeBind(@NotNull String key, @RequestAttribute Integer userId) {
        chatKeyService.changeBind(key, userId);
        return ResultEnum.SUCCESS.getResult();
    }

}
