package com.dataquad.mymulyamessage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private String senderId;
    private String recipientId;
    private String content;
    private String messageType;
    private Long fileId;
    private LocalDateTime sentAt;
}