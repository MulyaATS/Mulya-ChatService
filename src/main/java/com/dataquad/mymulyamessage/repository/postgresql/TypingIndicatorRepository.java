package com.dataquad.mymulyamessage.repository.postgresql;

import com.dataquad.mymulyamessage.entity.postgresql.TypingIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypingIndicatorRepository extends JpaRepository<TypingIndicator, Long> {
    void deleteByUserId(String userId);
}