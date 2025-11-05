package com.dataquad.mymulyamessage.repository.postgresql;

import com.dataquad.mymulyamessage.entity.postgresql.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByOrderBySentAtDesc(Pageable pageable);
    
    List<Message> findByRecipientIdIsNullOrderBySentAtDesc(Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :user1 AND m.recipientId = :user2) OR " +
           "(m.senderId = :user2 AND m.recipientId = :user1) " +
           "ORDER BY m.sentAt DESC")
    List<Message> findDirectMessages(@Param("user1") String user1, 
                                   @Param("user2") String user2, 
                                   Pageable pageable);
    
    Optional<Message> findBySenderIdAndFileNameAndMessageType(String senderId, String fileName, String messageType);
}