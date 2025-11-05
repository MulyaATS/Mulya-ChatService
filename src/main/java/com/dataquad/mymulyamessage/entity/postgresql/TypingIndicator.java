package com.dataquad.mymulyamessage.entity.postgresql;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "typing_indicators")
@Data
public class TypingIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
}