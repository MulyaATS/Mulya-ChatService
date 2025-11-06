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
    
    @Column(name = "file_id")
    private Long fileId;
    
    @Column(name = "message_type")
    private String messageType = "TEXT";
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}