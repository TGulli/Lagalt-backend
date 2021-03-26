package com.noroff.lagalt.message.repository;

import com.noroff.lagalt.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
