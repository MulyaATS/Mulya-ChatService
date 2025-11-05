package com.dataquad.mymulyamessage.entity.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_details")
@Data
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "user_name")
    private String userName;
    
    private String email;
    
    private String status;
    
    // Getter for name compatibility with existing code
    public String getName() {
        return userName;
    }
    
    // Getter for id compatibility
    public String getId() {
        return userId;
    }
}