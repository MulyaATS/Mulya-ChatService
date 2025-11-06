package com.dataquad.mymulyamessage.controller;

import com.dataquad.mymulyamessage.entity.postgresql.FileEntity;
import com.dataquad.mymulyamessage.repository.postgresql.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileController {
    
    private final FileRepository fileRepository;
    private final String uploadDir = "uploads/";
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, 
                                          @RequestParam("senderId") String senderId,
                                          @RequestParam("chatType") String chatType,
                                          @RequestParam(value = "recipientId", required = false) String recipientId) {
        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Save file to disk
            Files.copy(file.getInputStream(), filePath);
            
            // Save file info to database
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(fileName);
            fileEntity.setOriginalName(file.getOriginalFilename());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFilePath(filePath.toString());
            fileEntity.setUploadedBy(senderId);
            fileEntity.setChatType(chatType);
            fileEntity.setRecipientId(recipientId);
            fileEntity.setUploadedAt(LocalDateTime.now());
            
            FileEntity savedFile = fileRepository.save(fileEntity);
            return ResponseEntity.ok(savedFile.getId().toString());
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            FileEntity fileEntity = fileRepository.findById(fileId).orElse(null);
            if (fileEntity == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(fileEntity.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileEntity.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + fileEntity.getOriginalName() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}