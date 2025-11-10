package com.thederailingmafia.carwash.bookingservice.service;

import com.thederailingmafia.carwash.bookingservice.model.ChatMessage;
import com.thederailingmafia.carwash.bookingservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(String orderId) {
        return chatMessageRepository.findByOrderIdOrderByTimestampAsc(orderId);
    }

    public void markMessagesAsRead(String orderId, String userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByOrderIdAndReceiverIdAndIsReadFalse(orderId, userId);
        
        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    public long getUnreadMessageCount(String orderId, String userId) {
        return chatMessageRepository.countByOrderIdAndReceiverIdAndIsReadFalse(orderId, userId);
    }
}