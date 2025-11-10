package com.vector_search_service.ai.controller;


import com.vector_search_service.ai.dto.ChatbotRequest;
import com.vector_search_service.ai.dto.UserProfileResponse;
import com.vector_search_service.ai.service.*;
import com.vector_search_service.ai.tools.SystemTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private ChatModel chatModel;

    @Autowired(required = false)
    private List<Object> allBeans = List.of();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VertexAIService vertexAIService;

    @Autowired
    private PdfReaderService pdfReaderService;

    @Autowired
    private RetrievalService retrievalService;

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private SystemTools systemTools;

    @Autowired
    private SpeechService speechService;


    // In-memory chat storage
    private final Map<String, List<String>> chatHistory = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public String healthCheck() {
        return "health is fine";
    }

    @GetMapping("/test")
    public String testLLM() {
        return chatModel.call("What is Java?");
    }

    @GetMapping("/answer")
    public String getAnswer(@RequestParam String query) throws Exception {
        // RAG-only endpoint - for PDF document queries
        List<String> docs = retrievalService.searchSimilarDocs(query, 3);

        String context = docs.isEmpty() ? "No relevant documents found." :
                String.join("\n---\n", docs);

        String prompt = "You are a helpful assistant for car wash service documentation. Use only the following documents to answer:\n\n"
                + context + "\n\n"
                + "Question: " + query + "\n\n"
                + "If the answer is not in the documents, say 'I don't have information about that in the available documents.'";

        return ChatClient.create(chatModel)
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithBot(@RequestBody ChatbotRequest request) {
        try {
            // Validate user token
            System.out.println("request coming "  + request.getMessage());
            UserProfileResponse user = chatbotService.validateUser(request.getToken());

            // Set token in SystemTools for tool calls
            systemTools.setUserToken(request.getToken());

            // Create enhanced prompt based on user role
            String roleContext = getRoleContext(user.getUserRole());
            String prompt = roleContext + "\n\nUser message: " + request.getMessage();

            // Use only systemTools to avoid duplicates
            Object[] toolBeans = new Object[]{systemTools};

            String response = ChatClient.create(chatModel)
                    .prompt()
                    .user(prompt)
                    .tools(toolBeans)
                    .call()
                    .content();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/vertex-chat")
    public ResponseEntity<String> vertexChat(@RequestParam String message, @RequestParam(required = false) String sessionId) {
        try {
            // Use default session if not provided
            String session = sessionId != null ? sessionId : "default";

            // Get or create chat history for this session
            List<String> history = chatHistory.computeIfAbsent(session, k -> new ArrayList<>());

            // Build context with chat history
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("You are a helpful AI assistant for a car wash service. ");

            if (!history.isEmpty()) {
                contextBuilder.append("Previous conversation:\n");
                for (int i = Math.max(0, history.size() - 10); i < history.size(); i++) {
                    contextBuilder.append(history.get(i)).append("\n");
                }
                contextBuilder.append("\n");
            }

            contextBuilder.append("Current question: ").append(message);

            String response = ChatClient.create(chatModel)
                    .prompt()
                    .user(contextBuilder.toString())
                    .call()
                    .content();

            // Store conversation in history
            history.add("User: " + message);
            history.add("Assistant: " + response);

            // Keep only last 10 messages to prevent memory issues
            if (history.size() > 10) {
                history.subList(0, history.size() - 10).clear();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/vertex-chat/clear")
    public ResponseEntity<String> clearChatHistory(@RequestParam(required = false) String sessionId) {
        String session = sessionId != null ? sessionId : "default";
        chatHistory.remove(session);
        return ResponseEntity.ok("Chat history cleared for session: " + session);
    }

    private String getRoleContext(String userRole) {
        switch (userRole.toUpperCase()) {
            case "CUSTOMER":
                return "You are a helpful car wash service assistant for customers. You can help with:\n" +
                        "- Booking car washes (immediate or scheduled)\n" +
                        "- Viewing and managing my bookings\n" +
                        "- Checking my car information\n" +
                        "- Finding available washers\n" +
                        "- Canceling my bookings\n" +
                        "Always be friendly and helpful. If user wants to book but doesn't specify a car, show their cars first using getMyCars tool.";
            case "WASHER":
                return "You are a helpful assistant for car wash service providers (washers). You can help with:\n" +
                        "- Viewing my pending wash requests using showMyRequests\n" +
                        "- Accepting or declining wash requests\n" +
                        "- Marking orders as completed\n" +
                        "- Managing work schedule\n" +
                        "Always be professional and efficient in helping manage wash requests.";
            case "ADMIN":
                return "You are an administrative assistant for the car wash service. You have access to system-wide information and can help with general inquiries.";
            default:
                return "You are a helpful car wash service assistant.";
        }
    }

    @PostMapping("/order/ingest-text")
    public ResponseEntity<String> ingestText(@RequestParam String text,
                                             @RequestParam String filename) throws Exception {

        List<Double> embedding = vertexAIService.getEmbedding(text);

        String vectorLiteral = embedding.toString()
                .replace("[", "[")
                .replace("]", "]");

        jdbcTemplate.update(
                "INSERT INTO document_embeddings (filename, content, embedding) VALUES (?, ?, ?::vector)",
                filename, text, vectorLiteral
        );

        return ResponseEntity.ok("Text ingested successfully");
    }

    @PostMapping("/order/ingest-pdf")
    public ResponseEntity<String> ingestPdf(@RequestParam MultipartFile file) throws Exception {
        String text = pdfReaderService.extractText(file);
        return ingestText(text, file.getOriginalFilename());
    }



    // 🔊 Text → Speech
    @PostMapping("/text-to-speech")
    public ResponseEntity<String> textToSpeech(@RequestBody String text) throws Exception {
        String outputFile = speechService.textToSpeech(text);
        return ResponseEntity.ok("✅ Speech generated successfully. Access file at: /api/speech/audio");
    }

    // 🎧 Serve generated audio
    @GetMapping("/audio")
    public ResponseEntity<Resource> getAudio() {
        File file = new File("output.mp3");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }
    // 🎤 Speech → Text
    @PostMapping("/speech-to-text")
    public ResponseEntity<String> speechToText() {
        try {
            // Record 5 seconds of audio from mic
            byte[] audioBytes = speechService.recordFromMicrophone(5);

            // Call speech service using raw bytes
            String text = speechService.speechBytesToText(audioBytes);

            System.out.println("📝 Transcribed text: " + text);
            return ResponseEntity.ok("📝 Transcribed text: " + text);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("❌ Error during speech-to-text: " + e.getMessage());
        }
    }


}


