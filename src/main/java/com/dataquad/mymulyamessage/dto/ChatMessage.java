package com.dataquad.mymulyamessage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private String senderId;
    private String recipientId;
    private String content;
    private String messageType;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime sentAt;
}