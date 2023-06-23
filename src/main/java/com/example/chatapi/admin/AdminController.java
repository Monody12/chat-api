package com.example.chatapi.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.chatapi.mapper.*;
import com.example.chatapi.model.entity.*;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatKeyMapper chatKeyMapper;
    @Autowired
    private ChatHistoryMapper chatHistoryMapper;
    @Autowired
    private ChatTopicMapper chatTopicMapper;
    @Autowired
    private ChatKeyBindMapper chatKeyBindMapper;

    @GetMapping("/user/all")
    public Result getAllUser() {
        List<User> users = userMapper.selectList(null);
        return ResultEnum.SUCCESS.setData(users).getResult();
    }

    @PutMapping("/user")
    public Result updateUser(@RequestBody User user) {
        int update = userMapper.updateById(user);
        if (update == 0) {
            return ResultEnum.PARAM_ERROR.setMsg("未更新数据").getResult();
        }
        return ResultEnum.SUCCESS.setData(null).getResult();
    }

    @DeleteMapping("/user/{id}")
    public Result deleteUser(@PathVariable int id) {
        int i = userMapper.deleteById(id);
        if (i == 0) {
            return ResultEnum.NOT_FOUND.getResult();
        }
        return ResultEnum.SUCCESS.setData(null).setMsg("删除成功").getResult();
    }

    @GetMapping("/key/all")
    public Result getAllKey() {
        List<ChatKey> chatKeys = chatKeyMapper.selectList(null);
        return ResultEnum.SUCCESS.setData(chatKeys).getResult();
    }

    @PutMapping("/key")
    public Result updateKey(@RequestBody ChatKey key){
        chatKeyMapper.updateById(key);
        return ResultEnum.SUCCESS.getResult();
    }

    @DeleteMapping("/key/{id}")
    public Result deleteKey(@PathVariable int id){
        int i = chatKeyMapper.deleteById(id);
        if (i == 0) {
            return ResultEnum.NOT_FOUND.getResult();
        }
        return ResultEnum.SUCCESS.setData(null).setMsg("删除成功").getResult();
    }

    @GetMapping("/chat/topic/all")
    public Result getAllChatTopic() {
        List<ChatTopic> chatTopics = chatTopicMapper.selectList(null);
        return ResultEnum.SUCCESS.setData(chatTopics).getResult();
    }

    @PutMapping("/chat/topic")
    public Result updateChatTopic(@RequestBody ChatTopic topic) {
        int update = chatTopicMapper.updateById(topic);
        if (update == 0) {
            return ResultEnum.PARAM_ERROR.setMsg("未更新数据").getResult();
        }
        return ResultEnum.SUCCESS.setData(null).getResult();
    }

    @DeleteMapping("/chat/topic/{id}")
    public Result deleteChatTopic(@PathVariable int id) {
        int i = chatTopicMapper.deleteById(id);
        if (i == 0) {
            return ResultEnum.NOT_FOUND.getResult();
        }else{
            LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatHistory::getTopicId,id);
            chatHistoryMapper.delete(queryWrapper);
        }
        return ResultEnum.SUCCESS.setData(null).setMsg("删除成功").getResult();
    }

    @GetMapping("/chat/topic/{id}")
    public Result getChatHistory(@PathVariable int id){
        LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatHistory::getTopicId,id);
        List<ChatHistory> chatHistories = chatHistoryMapper.selectList(queryWrapper);
        return ResultEnum.SUCCESS.setData(chatHistories).getResult();
    }

    @GetMapping("/chat/key/bind/all")
    public Result getAllKeyBind(){
        List<ChatKeyBind> chatKeyBinds = chatKeyBindMapper.selectList(null);
        return ResultEnum.SUCCESS.setData(chatKeyBinds).getResult();
    }

    @PutMapping("/chat/key/bind")
    public Result updateKeyBind(@RequestBody ChatKeyBind chatKeyBind){
        chatKeyBindMapper.updateById(chatKeyBind);
        return ResultEnum.SUCCESS.getResult();
    }


    @DeleteMapping("/chat/key/bind/{id}")
    public Result deleteKeyBind(@PathVariable int id){
        ChatKeyBind chatKeyBind = chatKeyBindMapper.selectById(id);
        LambdaUpdateWrapper<User>updateWrapper = new LambdaUpdateWrapper<>();
        // 删之前把用户记录改为null
        updateWrapper.eq(User::getId, chatKeyBind.getUserId()).set(User::getKey, null);
        chatKeyBindMapper.deleteById(id);
        return ResultEnum.SUCCESS.getResult();
    }
    
    // insert
    @PostMapping("/user")
    public Result insertUser(@RequestBody User user){
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return ResultEnum.SUCCESS.setData(user).getResult();
    }


    @PostMapping("/chat/key")
    public Result insertKey(@RequestBody ChatKey chatKey){
        chatKey.setCreateTime(LocalDateTime.now());
        chatKeyMapper.insert(chatKey);
        return ResultEnum.SUCCESS.setData(chatKey).getResult();
    }


    @PostMapping("/chat/key/bind")
    public Result insertKeyBind(@RequestBody ChatKeyBind chatKeyBind){
        chatKeyBind.setCreateTime(LocalDateTime.now());
        chatKeyBindMapper.insert(chatKeyBind);
        return ResultEnum.SUCCESS.setData(chatKeyBind).getResult();
    }

    @Autowired
    private FeedbackMapper feedbackMapper;

    @PutMapping("/feedback")
    public Result updateFeedback(@RequestBody Feedback feedback){
        feedbackMapper.updateById(feedback);
        return ResultEnum.SUCCESS.getResult();
    }

    @GetMapping("/feedback/all")
    public Result getAllFeedback(){
        List<Feedback> feedbacks = feedbackMapper.selectList(null);
        return ResultEnum.SUCCESS.setData(feedbacks).getResult();
    }

    @DeleteMapping("/feedback/{id}")
    public Result deleteFeedback(@PathVariable Integer id){
        feedbackMapper.deleteById(id);
        return ResultEnum.SUCCESS.getResult();
    }
}
