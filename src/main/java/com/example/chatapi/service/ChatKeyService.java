package com.example.chatapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.chatapi.model.entity.ChatKey;

import java.util.List;

public interface ChatKeyService extends IService<ChatKey> {

    void checkExist(String key);

    /**
     * 管理员：变更key使用剩余次数
     * @return 变更后的剩余次数
     */
    int keyTimesChange(Integer keyId, int times);

    /**
     * 用户：使用一次key
     * 检查：key是否存在，key是否已经使用完
     * @param userId
     */
    int keyUseOnce(Integer userId);

    /**
     * 用户：查询key的剩余次数
     */
    int keyTimes(Integer userId);

    /**
     * 内部：查询key的剩余次数
     */
    int keyTimes(String key);


    /**
     * 查询key绑定的用户数量
     */
    int keyBindUserCount(Integer keyId);

    int keyBindUserCount(String key);

    /**
     * 用户注册、用户换绑：新增key与用户的绑定关系
     * @param key
     * @param userId
     */
    void keyBindUser(String key, Integer userId);

    /**
     * 删除key与用户的绑定关系
     */
    void keyUnbindUser(Integer keyId, Integer userId);

    void keyUnbindUser(String key, Integer userId);

    /**
     * 管理员：创建新key
     */
    List<ChatKey> createKey(int times,int count);

    /**
     * 管理员：批量删除key与绑定关系
     * @return 删除的key数量
     */
    int deleteKey(List<Integer> keyIds);

    /**
     * 管理员：查询key列表，支持mybatis plus分页
     */
    List<ChatKey> getKeyList(int page, int size);

    /**
     * 管理员：删除次数为0的key，以及与用户的绑定关系
     * @return 删除的key数量
     */
    int deleteKeyTimesZero();

    /**
     * 用户：换绑key
     * 1. 检查新key是否存在、剩余次数>0、绑定次数不超上限
     * 2. 删除旧key与用户的绑定关系
     * 3. 新增新key与用户的绑定关系
     */
    void changeBind(String key, Integer userId);
}
