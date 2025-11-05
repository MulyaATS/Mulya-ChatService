package com.dataquad.mymulyamessage.repository.mysql;

import com.dataquad.mymulyamessage.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByStatus(String status);
}