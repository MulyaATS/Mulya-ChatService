package com.dataquad.mymulyamessage.entity.postgresql;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
}