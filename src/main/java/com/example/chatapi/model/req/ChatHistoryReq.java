package com.example.chatapi.model.req;


import com.example.chatapi.model.entity.ChatHistory;
import lombok.Data;

import java.util.List;

@Data
public class ChatHistoryReq {

    private Integer topicId;
    private List<ChatHistory> chatHistoryList;

}
