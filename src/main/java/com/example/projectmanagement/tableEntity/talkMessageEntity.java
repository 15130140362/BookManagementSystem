package com.example.projectmanagement.tableEntity;

import lombok.Data;

@Data
public class talkMessageEntity {
    private Integer messageType;//0表示发送的公告消息，1表示聊天消息
    private String senderName;
    private String messageContent;
    private String time;
    private String announceId;
}
