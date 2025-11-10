package com.theDetailingMafia.media_service.controller;


import com.theDetailingMafia.media_service.model.MediaMetadata;
import com.theDetailingMafia.media_service.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media Service", description = "Media upload and retrieval APIs")
public class MediaController {


    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Media Service is up and running!";
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload File", description = "Uploads a file to the media service")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam String entityId,
                                         @RequestParam String entityType,
                                         Authentication authentication) throws IOException {
        String uploadedBy = authentication.getName();
        String fileId = mediaService.uploadFile(file, entityId, entityType, uploadedBy);
        return ResponseEntity.ok(fileId);
    }

    @PostMapping("/profile/upload")
    @Operation(summary = "Upload Profile Image", description = "Uploads profile image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file,
                                                    Authentication authentication) throws IOException {
        String userId = authentication.getName();
        String fileId = mediaService.uploadFile(file, userId, "PROFILE", userId);
        return ResponseEntity.ok(fileId);
    }

    @PostMapping("/car/upload")
    @Operation(summary = "Upload Car Image", description = "Uploads car image")
    public ResponseEntity<String> uploadCarImage(@RequestParam("file") MultipartFile file,
                                               @RequestParam String carId,
                                               Authentication authentication) throws IOException {
        String userId = authentication.getName();
        String fileId = mediaService.uploadFile(file, carId, "CAR", userId);
        return ResponseEntity.ok(fileId);
    }

    @PostMapping("/service/before")
    @Operation(summary = "Upload Before Service Image", description = "Uploads before service image")
    public ResponseEntity<String> uploadBeforeServiceImage(@RequestParam("file") MultipartFile file,
                                                          @RequestParam String orderId,
                                                          Authentication authentication) throws IOException {
        String washerId = authentication.getName();
        String fileId = mediaService.uploadFile(file, orderId, "SERVICE_BEFORE", washerId);
        return ResponseEntity.ok(fileId);
    }

    @PostMapping("/service/after")
    @Operation(summary = "Upload After Service Image", description = "Uploads after service image")
    public ResponseEntity<String> uploadAfterServiceImage(@RequestParam("file") MultipartFile file,
                                                         @RequestParam String orderId,
                                                         Authentication authentication) throws IOException {
        String washerId = authentication.getName();
        String fileId = mediaService.uploadFile(file, orderId, "SERVICE_AFTER", washerId);
        return ResponseEntity.ok(fileId);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileId) throws IOException {
        GridFsResource resource = mediaService.getFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @GetMapping("/display/{fileId}")
    public ResponseEntity<InputStreamResource> displayImage(@PathVariable String fileId) throws IOException {
        try {
            System.out.println("Attempting to retrieve file with ID: " + fileId);
            GridFsResource resource = mediaService.getFile(fileId);
            if (!resource.exists()) {
                System.out.println("GridFS resource does not exist for ID: " + fileId);
                return ResponseEntity.notFound().build();
            }
            System.out.println("Successfully found GridFS resource for ID: " + fileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resource.getContentType()))
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error retrieving file " + fileId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<MediaMetadata>> getFilesByEntity(@PathVariable String entityId) {
        List<MediaMetadata> files = mediaService.getFilesByEntity(entityId);
        System.out.println("Found " + files.size() + " files for entity: " + entityId);
        files.forEach(file -> System.out.println("File: " + file.getId() + ", Type: " + file.getEntityType()));
        return ResponseEntity.ok(files);
    }
    
    @GetMapping("/debug/all")
    public ResponseEntity<List<MediaMetadata>> getAllFiles() {
        return ResponseEntity.ok(mediaService.getAllFiles());
    }
    
    @PostMapping("/test/upload")
    public ResponseEntity<String> testUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileId = mediaService.uploadFile(file, "test-entity", "TEST", "test-user");
        return ResponseEntity.ok("Uploaded with ID: " + fileId);
    }
    
    @DeleteMapping("/cleanup/orphaned")
    public ResponseEntity<String> cleanupOrphanedMetadata() {
        int cleaned = mediaService.cleanupOrphanedMetadata();
        return ResponseEntity.ok("Cleaned up " + cleaned + " orphaned metadata entries");
    }
}

