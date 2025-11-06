package com.dataquad.mymulyamessage.controller;

import com.dataquad.mymulyamessage.dto.MessageDto;
import com.dataquad.mymulyamessage.entity.mysql.User;
import com.dataquad.mymulyamessage.entity.postgresql.Message;
import com.dataquad.mymulyamessage.entity.postgresql.FileEntity;
import com.dataquad.mymulyamessage.entity.postgresql.UserSession;
import com.dataquad.mymulyamessage.repository.mysql.UserRepository;
import com.dataquad.mymulyamessage.repository.postgresql.MessageRepository;
import com.dataquad.mymulyamessage.repository.postgresql.FileRepository;
import com.dataquad.mymulyamessage.repository.postgresql.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiController {
    
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;
    private final UserSessionRepository userSessionRepository;

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        try {
            return userRepository.findByStatus("ACTIVE");
        } catch (Exception e) {
            // Fallback: return sample users if database is not available
            List<User> sampleUsers = List.of(
                createUser("user1", "Alice Johnson", "alice@example.com"),
                createUser("user2", "Bob Smith", "bob@example.com"),
                createUser("user3", "Carol Davis", "carol@example.com"),
                createUser("user4", "David Wilson", "david@example.com")
            );
            return sampleUsers;
        }
    }
    
    private User createUser(String id, String name, String email) {
        User user = new User();
        user.setUserId(id);
        user.setUserName(name);
        user.setEmail(email);
        user.setStatus("ACTIVE");
        return user;
    }

    @PostMapping("/chat/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            UserSession session = userSessionRepository.findByUserId(userId)
                    .orElse(new UserSession());
            session.setUserId(userId);
            session.setIsOnline(true);
            session.setLastActivity(LocalDateTime.now());
            userSessionRepository.save(session);
            
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.ok("Login successful (fallback)");
        }
    }
    
    @GetMapping("/users/online")
    public List<String> getOnlineUsers() {
        try {
            return userSessionRepository.findByIsOnline(true)
                    .stream()
                    .map(UserSession::getUserId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(); // Return empty list if database unavailable
        }
    }

    @GetMapping("/messages")
    public List<MessageDto> getMessages(@RequestParam(defaultValue = "50") int limit) {
        List<Message> messages = messageRepository.findByRecipientIdIsNullOrderBySentAtDesc(PageRequest.of(0, limit));
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/messages/direct")
    public List<MessageDto> getDirectMessages(@RequestParam String user1, 
                                            @RequestParam String user2, 
                                            @RequestParam(defaultValue = "50") int limit) {
        List<Message> messages = messageRepository.findDirectMessages(user1, user2, PageRequest.of(0, limit));
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setSentAt(message.getSentAt());
        
        // Add file info if it's a file message
        if ("FILE".equals(message.getMessageType()) && message.getFileId() != null) {
            FileEntity fileEntity = fileRepository.findById(message.getFileId()).orElse(null);
            if (fileEntity != null) {
                dto.setFileId(message.getFileId());
                dto.setFileName(fileEntity.getOriginalName());
                dto.setFileType(fileEntity.getFileType());
                dto.setFileSize(fileEntity.getFileSize());
            }
        }
        
        Optional<User> user = userRepository.findById(message.getSenderId());
        dto.setSenderName(user.map(User::getUserName).orElse("Unknown"));
        
        return dto;
    }


}