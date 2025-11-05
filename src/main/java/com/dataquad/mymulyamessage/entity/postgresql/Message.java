package com.dataquad.mymulyamessage.entity.postgresql;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sender_id")
    private String senderId;
    
    @Column(name = "recipient_id")
    private String recipientId;
    
    private String content;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;
    
    @Column(name = "message_type")
    private String messageType = "TEXT";
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}