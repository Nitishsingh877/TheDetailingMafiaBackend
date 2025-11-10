package com.thederailingmafia.carwash.bookingservice.repository;

import com.thederailingmafia.carwash.bookingservice.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByOrderIdOrderByTimestampAsc(String orderId);
    
    List<ChatMessage> findByOrderIdAndReceiverIdAndIsReadFalse(String orderId, String receiverId);
    
    long countByOrderIdAndReceiverIdAndIsReadFalse(String orderId, String receiverId);
}