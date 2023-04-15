package com.example.chatapi;

import com.example.chatapi.model.entity.ChatHistory;
import com.example.chatapi.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ChatApiApplicationTests {

    @Autowired
    private ChatService chatService;

    @Test
    void contextLoads() {
    }

    @Test
    void testChat() {
        List<ChatHistory> chatHistoryList = new ArrayList<>();
        ChatHistory u1 = new ChatHistory();
        u1.setUserId(1);
        u1.setTopicId(1);
        u1.setRole("user");
        u1.setContent("你知道java中有哪些包装类吗？");
        ChatHistory a1 = new ChatHistory();
        a1.setUserId(1);
        a1.setTopicId(1);
        a1.setRole("assistant");
        a1.setContent("Java中的包装类有：\n" +
                "\n" +
                "1. Byte：用于表示字节类型的对象\n" +
                "2. Short：用于表示短整型类型的对象\n" +
                "3. Integer：用于表示整型类型的对象\n" +
                "4. Long：用于表示长整型类型的对象\n" +
                "5. Float：用于表示浮点型类型的对象\n" +
                "6. Double：用于表示双精度浮点型类型的对象\n" +
                "7. Character：用于表示字符类型的对象\n" +
                "8. Boolean：用于表示布尔类型的对象\n" +
                "\n" +
                "这些包装类都是为了将基本数据类型转换为对象类型而存在的。它们提供了一些常用的方法，如valueOf()方法，用于将基本数据类型转换为对象类型；还有parseXXX()方法，用于将字符串转换为对应的基本数据类型。同时，包装类也提供了一些常用的操作，如比较大小、转换进制等。");
        ChatHistory u2 = new ChatHistory();
        u2.setUserId(1);
        u2.setTopicId(1);
        u2.setRole("user");
        u2.setContent("使用Long的做一个示范");
        chatHistoryList.add(u1);
        chatHistoryList.add(a1);
        chatHistoryList.add(u2);
        List<ChatHistory> res = chatService.chat(3, chatHistoryList);
        System.out.println(res);
    }


}
