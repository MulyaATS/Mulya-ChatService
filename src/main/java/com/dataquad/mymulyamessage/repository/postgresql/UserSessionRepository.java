package com.dataquad.mymulyamessage.repository.postgresql;

import com.dataquad.mymulyamessage.entity.postgresql.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserId(String userId);
    List<UserSession> findByIsOnline(Boolean isOnline);
}