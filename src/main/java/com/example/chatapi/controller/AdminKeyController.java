package com.example.chatapi.controller;

import com.example.chatapi.model.entity.ChatKey;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import com.example.chatapi.service.ChatKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/key")
public class AdminKeyController {

    @Autowired
    private ChatKeyService chatKeyService;

    @GetMapping("/create")
    public Result createKey(int times, int count) {
        List<ChatKey> chatKeys = chatKeyService.createKey(times, count);
        log.info("创建key成功，key: {}", chatKeys);
        return ResultEnum.SUCCESS.setData(chatKeys).getResult();
    }

    @GetMapping("/update")
    public Result keyTimesChange(Integer keyId, int times) {
        int keyTimes = chatKeyService.keyTimesChange(keyId, times);
        return ResultEnum.SUCCESS.setData(keyTimes).getResult();
    }

}
