package com.dataquad.mymulyamessage.entity.postgresql;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "original_name", nullable = false)
    private String originalName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;
    
    @Column(name = "chat_type", nullable = false)
    private String chatType; // "GENERAL" or "DIRECT"
    
    @Column(name = "recipient_id")
    private String recipientId; // null for general, userId for direct
    
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}