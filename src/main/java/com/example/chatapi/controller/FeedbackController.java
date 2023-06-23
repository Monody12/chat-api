package com.example.chatapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.chatapi.mapper.FeedbackMapper;
import com.example.chatapi.model.entity.Feedback;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @PostMapping("")
    public Result insert(@RequestBody Feedback feedback) {
        feedbackMapper.insert(feedback);
        return ResultEnum.SUCCESS.getResult();
    }

    @GetMapping("")
    public Result getList(@RequestParam Integer userId) {
        LambdaQueryWrapper<Feedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Feedback::getUserId, userId);
        List<Feedback> feedbacks = feedbackMapper.selectList(queryWrapper);
        return ResultEnum.SUCCESS.setData(feedbacks).getResult();
    }
}
