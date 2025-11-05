package com.dataquad.mymulyamessage.controller;

import com.dataquad.mymulyamessage.dto.ChatMessage;
import com.dataquad.mymulyamessage.dto.MessageDto;
import com.dataquad.mymulyamessage.entity.mysql.User;
import com.dataquad.mymulyamessage.entity.postgresql.Message;
import com.dataquad.mymulyamessage.repository.mysql.UserRepository;
import com.dataquad.mymulyamessage.repository.postgresql.MessageRepository;
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
    private final TypingIndicatorRepository typingIndicatorRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        // Stop typing indicator when message is sent
        try {
            Optional<User> user = userRepository.findById(chatMessage.getSenderId());
            String userName = user.map(User::getUserName).orElse("Unknown");
            String typingData = "{\"userId\":\"" + chatMessage.getSenderId() + "\",\"userName\":\"" + userName + "\",\"isTyping\":false}";
            
            if (chatMessage.getRecipientId() != null) {
                messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/typing", typingData);
            } else {
                messagingTemplate.convertAndSend("/topic/typing", typingData);
            }
        } catch (Exception e) {
            // Ignore typing stop error
        }
        
        Message savedMessage;
        
        if ("FILE".equals(chatMessage.getMessageType())) {
            // For file messages, find the existing message created by upload API
            Optional<Message> existingFile = messageRepository.findBySenderIdAndFileNameAndMessageType(
                chatMessage.getSenderId(), chatMessage.getFileName(), "FILE");
            
            if (existingFile.isPresent()) {
                savedMessage = existingFile.get();
            } else {
                // Fallback: create new message if not found
                Message message = new Message();
                message.setSenderId(chatMessage.getSenderId());
                message.setContent(chatMessage.getContent());
                message.setMessageType(chatMessage.getMessageType());
                message.setRecipientId(chatMessage.getRecipientId());
                message.setFileName(chatMessage.getFileName());
                message.setFileType(chatMessage.getFileType());
                message.setFileSize(chatMessage.getFileSize());
                message.setSentAt(LocalDateTime.now());
                savedMessage = messageRepository.save(message);
            }
        } else {
            // For text messages, save normally
            Message message = new Message();
            message.setSenderId(chatMessage.getSenderId());
            message.setContent(chatMessage.getContent());
            message.setMessageType(chatMessage.getMessageType());
            message.setRecipientId(chatMessage.getRecipientId());
            message.setSentAt(LocalDateTime.now());
            savedMessage = messageRepository.save(message);
        }
        
        MessageDto messageDto = new MessageDto();
        messageDto.setId(savedMessage.getId());
        messageDto.setSenderId(savedMessage.getSenderId());
        messageDto.setRecipientId(savedMessage.getRecipientId());
        messageDto.setContent(savedMessage.getContent());
        messageDto.setMessageType(savedMessage.getMessageType());
        messageDto.setFileName(savedMessage.getFileName());
        messageDto.setFileType(savedMessage.getFileType());
        messageDto.setFileSize(savedMessage.getFileSize());
        messageDto.setSentAt(savedMessage.getSentAt());
        
        Optional<User> user = userRepository.findById(savedMessage.getSenderId());
        messageDto.setSenderName(user.map(User::getUserName).orElse("Unknown"));
        
        // Add fileId for file messages
        if ("FILE".equals(savedMessage.getMessageType())) {
            // For file messages, use the message ID as fileId for download
            messageDto.setContent(savedMessage.getContent()); // Keep the file icon + name format
        }
        
        // Route message based on type
        if (savedMessage.getRecipientId() != null) {
            // Direct message - send to both sender and recipient
            messagingTemplate.convertAndSendToUser(savedMessage.getSenderId(), "/queue/messages", messageDto);
            messagingTemplate.convertAndSendToUser(savedMessage.getRecipientId(), "/queue/messages", messageDto);
        } else {
            // General message - send to general topic
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