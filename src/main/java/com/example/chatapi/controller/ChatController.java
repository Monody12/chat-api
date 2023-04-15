package com.example.chatapi.controller;

import com.example.chatapi.model.entity.ChatHistory;
import com.example.chatapi.model.entity.ChatTopic;
import com.example.chatapi.model.req.ChatHistoryReq;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import com.example.chatapi.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*",maxAge = 3600)
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/test")
    public Result test(HttpServletRequest request) {
        // 获取所有请求体
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            log.info("name:{} value:{}", name, request.getParameter(name));
        }
        return ResultEnum.SUCCESS.getResult();
    }


    @PostMapping("")
    public Result chat(@RequestHeader Integer userId,@RequestBody ChatHistoryReq chatHistoryReq) {
        List<ChatHistory> historyList = chatHistoryReq.getChatHistoryList();
        historyList.get(0).setTopicId(chatHistoryReq.getTopicId());
        historyList.forEach(chatHistory -> chatHistory.setUserId(userId));
        List<ChatHistory> chat = chatService.chat(userId, chatHistoryReq.getChatHistoryList());
        return ResultEnum.SUCCESS.setData(chat).getResult();
    }

    @GetMapping("/history")
    public Result getHistory(@RequestHeader Integer userId, @NotNull Integer topicId) {
        List<ChatHistory> chatHistoryList = chatService.getChatHistory(userId, topicId);
        return ResultEnum.SUCCESS.setData(chatHistoryList).getResult();
    }

    @GetMapping("/topic/new")
    public Result newTopic(@RequestHeader Integer userId,String topicName) {
        ChatTopic chatTopic = chatService.newTopic(userId, topicName);
        return ResultEnum.SUCCESS.setData(chatTopic).getResult();
    }

    @GetMapping("/topic/rename")
    public Result renameTopic(@RequestHeader Integer userId, @NotNull Integer topicId, String topicName) {
        chatService.renameChatTopic(userId, topicId, topicName);
        return ResultEnum.SUCCESS.getResult();
    }

    @GetMapping("/topic")
    public Result getTopic(@RequestHeader Integer userId) {
        List<ChatTopic> chatTopicList = chatService.getChatTopic(userId);
        // 按id降序排序
        chatTopicList.sort((o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
        return ResultEnum.SUCCESS.setData(chatTopicList).getResult();
    }

    @GetMapping("/topic/delete")
    public Result deleteTopic(@RequestHeader Integer userId, @NotNull Integer topicId) {
        chatService.deleteChatTopic(userId, topicId);
        return ResultEnum.SUCCESS.getResult();
    }


}
