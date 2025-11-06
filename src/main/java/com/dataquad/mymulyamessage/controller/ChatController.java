package com.dataquad.mymulyamessage.controller;

import com.dataquad.mymulyamessage.dto.ChatMessage;
import com.dataquad.mymulyamessage.dto.MessageDto;
import com.dataquad.mymulyamessage.entity.mysql.User;
import com.dataquad.mymulyamessage.entity.postgresql.Message;
import com.dataquad.mymulyamessage.entity.postgresql.FileEntity;
import com.dataquad.mymulyamessage.repository.mysql.UserRepository;
import com.dataquad.mymulyamessage.repository.postgresql.MessageRepository;
import com.dataquad.mymulyamessage.repository.postgresql.FileRepository;
import com.dataquad.mymulyamessage.repository.postgresql.TypingIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final TypingIndicatorRepository typingIndicatorRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        String senderName = userRepository.findById(chatMessage.getSenderId())
            .map(User::getUserName).orElse("Unknown");
        
        // Save message to database
        Message message = new Message();
        message.setSenderId(chatMessage.getSenderId());
        message.setRecipientId(chatMessage.getRecipientId());
        message.setContent(chatMessage.getContent());
        message.setMessageType(chatMessage.getMessageType());
        message.setFileId(chatMessage.getFileId());
        message.setSentAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        
        // Create response DTO
        MessageDto messageDto = new MessageDto();
        messageDto.setId(savedMessage.getId());
        messageDto.setSenderId(savedMessage.getSenderId());
        messageDto.setRecipientId(savedMessage.getRecipientId());
        messageDto.setContent(savedMessage.getContent());
        messageDto.setMessageType(savedMessage.getMessageType());
        messageDto.setSentAt(savedMessage.getSentAt());
        messageDto.setSenderName(senderName);
        
        // Add file info if it's a file message
        if ("FILE".equals(savedMessage.getMessageType()) && savedMessage.getFileId() != null) {
            FileEntity fileEntity = fileRepository.findById(savedMessage.getFileId()).orElse(null);
            if (fileEntity != null) {
                messageDto.setFileId(savedMessage.getFileId());
                messageDto.setFileName(fileEntity.getOriginalName());
                messageDto.setFileType(fileEntity.getFileType());
                messageDto.setFileSize(fileEntity.getFileSize());
            }
        }
        
        // Broadcast message
        if (chatMessage.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(chatMessage.getSenderId(), "/queue/messages", messageDto);
            messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages", messageDto);
        } else {
            messagingTemplate.convertAndSend("/topic/general", messageDto);
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(ChatMessage typingMessage) {
        try {
            Optional<User> user = userRepository.findById(typingMessage.getSenderId());
            String userName = user.map(User::getUserName).orElse("Unknown");
            String typingData = "{\"userId\":\"" + typingMessage.getSenderId() + "\",\"userName\":\"" + userName + "\",\"isTyping\":true}";
            
            if (typingMessage.getRecipientId() != null) {
                // Direct message typing - send only to recipient
                messagingTemplate.convertAndSendToUser(typingMessage.getRecipientId(), "/queue/typing", typingData);
            } else {
                // General chat typing - send to general topic
                messagingTemplate.convertAndSend("/topic/typing", typingData);
            }
        } catch (Exception e) {
            // Fallback for typing indicator
            String typingData = "{\"userId\":\"" + typingMessage.getSenderId() + "\",\"userName\":\"Unknown\",\"isTyping\":true}";
            if (typingMessage.getRecipientId() != null) {
                messagingTemplate.convertAndSendToUser(typingMessage.getRecipientId(), "/queue/typing", typingData);
            } else {
                messagingTemplate.convertAndSend("/topic/typing", typingData);
            }
        }
    }

    @MessageMapping("/chat.stopTyping")
    public void handleStopTyping(ChatMessage typingMessage) {
        try {
            Optional<User> user = userRepository.findById(typingMessage.getSenderId());
            String userName = user.map(User::getUserName).orElse("Unknown");
            String typingData = "{\"userId\":\"" + typingMessage.getSenderId() + "\",\"userName\":\"" + userName + "\",\"isTyping\":false}";
            
            if (typingMessage.getRecipientId() != null) {
                // Direct message stop typing - send only to recipient
                messagingTemplate.convertAndSendToUser(typingMessage.getRecipientId(), "/queue/typing", typingData);
            } else {
                // General chat stop typing - send to general topic
                messagingTemplate.convertAndSend("/topic/typing", typingData);
            }
        } catch (Exception e) {
            // Fallback for stop typing indicator
            String typingData = "{\"userId\":\"" + typingMessage.getSenderId() + "\",\"userName\":\"Unknown\",\"isTyping\":false}";
            if (typingMessage.getRecipientId() != null) {
                messagingTemplate.convertAndSendToUser(typingMessage.getRecipientId(), "/queue/typing", typingData);
            } else {
                messagingTemplate.convertAndSend("/topic/typing", typingData);
            }
        }
    }
}