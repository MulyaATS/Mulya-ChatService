package com.dataquad.mymulyamessage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private String senderId;
    private String recipientId;
    private String senderName;
    private String content;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String messageType;
    private LocalDateTime sentAt;
    private Long fileId;
}