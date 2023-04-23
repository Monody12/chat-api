package com.example.chatapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.chatapi.exception.ExceptionEnum;
import com.example.chatapi.mapper.ChatKeyBindMapper;
import com.example.chatapi.mapper.ChatKeyMapper;
import com.example.chatapi.mapper.UserMapper;
import com.example.chatapi.model.entity.ChatKey;
import com.example.chatapi.model.entity.ChatKeyBind;
import com.example.chatapi.model.entity.User;
import com.example.chatapi.service.ChatKeyService;
import com.example.chatapi.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatKeyServiceImpl extends ServiceImpl<ChatKeyMapper, ChatKey> implements ChatKeyService{

    @Autowired
    private ChatKeyMapper chatKeyMapper;
    @Autowired
    private ChatKeyBindMapper chatKeyBindMapper;
    @Autowired
    private UserMapper userMapper;
    @Value("${chat.key.bind.max}")
    private Integer KEY_BIND_MAX;



    @Override
    public void checkExist(String key) {
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
    }

    /**
     * 变更key使用剩余次数
     *
     * @param times
     * @return 变更后的剩余次数
     */
    @Override
    public int keyTimesChange(Integer keyId, int times) {
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getId, keyId);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        chatKey.setTimes(chatKey.getTimes() + times);
        chatKeyMapper.updateById(chatKey);
        return chatKey.getTimes();
    }

    /**
     * 用户：使用一次key
     * 检查：key是否存在，key是否已经使用完
     * @param userId
     */
    @Override
    public int keyUseOnce(Integer userId) {
        User user = userMapper.selectById(userId);
        String key = user.getKey();
        // 查出key
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        // 检查key是否已经使用完
        if (chatKey.getTimes() <= 0) {
            ExceptionEnum.KEY_USE_LIMIT.throwException();
        }
        // key使用次数减一
        chatKey.setTimes(chatKey.getTimes() - 1);
        chatKeyMapper.updateById(chatKey);
        return chatKey.getTimes();
    }

    /**
     * 用户：查询key的剩余次数
     *
     * @param userId
     */
    @Override
    public int keyTimes(Integer userId) {
        // 查询出用户信息
        User user = userMapper.selectById(userId);
        String key = user.getKey();
        return this.keyTimes(key);
    }


    @Override
    public int keyTimes(String key) {
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        return chatKey.getTimes();
    }

    /**
     * 查询key绑定的用户数量
     *
     * @param keyId
     */
    @Override
    public int keyBindUserCount(Integer keyId) {
        LambdaQueryWrapper<ChatKeyBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKeyBind::getKeyId, keyId);
        return chatKeyBindMapper.selectCount(queryWrapper);
    }

    @Override
    public int keyBindUserCount(String key) {
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        return keyBindUserCount(chatKey.getId());
    }

    /**
     * 用户注册、换绑：新增key与用户的绑定关系，同时检测key绑定的用户数量是否超过上限
     * 会检测key是否存在
     */
    @Override
    public void keyBindUser(String key, Integer userId) {
        // 检测key是否存在
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        // 检测key绑定的用户数量是否超过上限
        int bindUserCount = this.keyBindUserCount(key);
        if (bindUserCount > KEY_BIND_MAX) {
            ExceptionEnum.KEY_USER_LIMIT.throwException();
        }
        ChatKeyBind chatKeyBind = new ChatKeyBind();
        chatKeyBind.setKeyId(chatKey.getId());
        chatKeyBind.setUserId(userId);
        chatKeyBindMapper.insert(chatKeyBind);
        // 更新用户绑定的key
        User user = new User();
        user.setId(userId);
        user.setKey(key);
        userMapper.updateById(user);
    }

    /**
     * 删除key与用户的绑定关系
     *
     */
    @Override
    public void keyUnbindUser(Integer keyId, Integer userId) {
        LambdaQueryWrapper<ChatKeyBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKeyBind::getKeyId, keyId);
        queryWrapper.eq(ChatKeyBind::getUserId, userId);
        chatKeyBindMapper.delete(queryWrapper);
    }

    @Override
    public void keyUnbindUser(String key, Integer userId) {
        LambdaQueryWrapper<ChatKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatKey::getKey, key);
        ChatKey chatKey = chatKeyMapper.selectOne(queryWrapper);
        if (chatKey == null) {
            ExceptionEnum.KEY_NOT_EXIST.throwException();
        }
        keyUnbindUser(chatKey.getId(), userId);
    }

    /**
     * 管理员：创建新key
     *
     * @param times
     * @param count
     */
    @Override
    public List<ChatKey> createKey(int times, int count) {
        List<ChatKey> chatKeys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ChatKey chatKey = new ChatKey();
            chatKey.setKey(UUIDUtils.getUUID());
            chatKey.setTimes(times);
            chatKeys.add(chatKey);
        }
        super.saveBatch(chatKeys);
        return chatKeys;
    }

    /**
     * 管理员：批量删除key与绑定关系
     *
     * @param keyIds
     * @return 删除的key数量
     */
    @Override
    public int deleteKey(List<Integer> keyIds) {
        return 0;
    }

    /**
     * 管理员：查询key列表，支持mybatis plus分页
     *
     * @param page
     * @param size
     */
    @Override
    public List<ChatKey> getKeyList(int page, int size) {
        return null;
    }

    /**
     * 管理员：删除次数为0的key，以及与用户的绑定关系
     *
     * @return 删除的key数量
     */
    @Override
    public int deleteKeyTimesZero() {
        return 0;
    }

    /**
     * 用户：换绑key
     * 1. 检查新key是否存在、剩余次数>0、绑定次数不超上限
     * 2. 删除旧key与用户的绑定关系
     * 3. 新增新key与用户的绑定关系
     *
     * @param key
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeBind(String key, Integer userId) {
        // 检查新key是否存在、剩余次数>0、绑定次数不超上限
        int times = this.keyTimes(key);
        if (times <= 0) {
            ExceptionEnum.KEY_USE_LIMIT.throwException();
        }
        int bindUserCount = this.keyBindUserCount(key);
        if (bindUserCount > KEY_BIND_MAX) {
            ExceptionEnum.KEY_USER_LIMIT.throwException();
        }
        // 删除旧key与用户的绑定关系
        User user = userMapper.selectById(userId);
        String oldKey = user.getKey();
        this.keyUnbindUser(oldKey, userId);
        // 新增新key与用户的绑定关系
        this.keyBindUser(key, userId);
    }
}
