package com.dataquad.mymulyamessage.repository.postgresql;

import com.dataquad.mymulyamessage.entity.postgresql.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByChatTypeAndUploadedBy(String chatType, String uploadedBy);
    List<FileEntity> findByChatTypeAndRecipientIdAndUploadedBy(String chatType, String recipientId, String uploadedBy);
}