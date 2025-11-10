package com.thederailingmafia.carwash.bookingservice.controller;

import com.thederailingmafia.carwash.bookingservice.model.ChatMessage;
import com.thederailingmafia.carwash.bookingservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
//@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.send/{orderId}")
    @SendTo("/topic/chat/{orderId}")
    public ChatMessage sendMessage(@DestinationVariable String orderId, ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setOrderId(orderId);
        return chatService.saveMessage(message);
    }

    @MessageMapping("/chat.join/{orderId}")
    @SendTo("/topic/chat/{orderId}")
    public ChatMessage joinChat(@DestinationVariable String orderId, ChatMessage message) {
        message.setType(ChatMessage.MessageType.SYSTEM);
        message.setMessage(message.getSenderId() + " joined the chat");
        message.setTimestamp(LocalDateTime.now());
        message.setOrderId(orderId);
        return message;
    }

    @GetMapping("/api/chat/{orderId}/history")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable String orderId) {
        return chatService.getChatHistory(orderId);
    }

    @PostMapping("/api/chat/{orderId}/read")
    @ResponseBody
    public void markMessagesAsRead(@PathVariable String orderId, @RequestParam String userId) {
        chatService.markMessagesAsRead(orderId, userId);
    }

    @PostMapping("/api/chat/{orderId}/send")
    @ResponseBody
    public ChatMessage sendChatMessage(@PathVariable String orderId, @RequestBody ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setOrderId(orderId);
        return chatService.saveMessage(message);
    }
}