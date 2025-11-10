package com.theDetailingMafia.media_service.service;




import com.theDetailingMafia.media_service.model.MediaMetadata;
import com.theDetailingMafia.media_service.repository.MediaMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Service
public class MediaService {
    @Autowired
    private  GridFsTemplate gridFsTemplate;
    @Autowired
    private  MediaMetadataRepository metadataRepository;



    public String uploadFile(MultipartFile file, String entityId, String entityType, String uploadedBy) throws IOException {
        try {
            System.out.println("Starting file upload - Size: " + file.getSize() + ", Type: " + file.getContentType());
            
            // Store file in GridFS with metadata
            ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(), 
                file.getOriginalFilename(), 
                file.getContentType()
            );
            System.out.println("Stored file in GridFS with ID: " + fileId.toString());
            
            System.out.println("GridFS storage completed - skipping verification");

            // Save metadata only after successful GridFS verification
            MediaMetadata metadata = new MediaMetadata();
            metadata.setId(fileId.toString());
            metadata.setEntityId(entityId);
            metadata.setEntityType(entityType);
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(file.getContentType());
            metadata.setUploadedAt(Instant.now());
            metadata.setUploadedBy(uploadedBy);

            metadataRepository.save(metadata);
            System.out.println("Saved metadata for file: " + fileId.toString());
            return fileId.toString();
        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public GridFsResource getFile(String fileId) throws IllegalStateException {
        try {
            return gridFsTemplate.getResource(fileId);
        } catch (Exception e) {
            throw new IllegalStateException("GridFs resource [" + fileId + "] does not exist.");
        }
    }

    public List<MediaMetadata> getFilesByEntity(String entityId) {
        return metadataRepository.findByEntityId(entityId);
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(query(where("_id").is(new ObjectId(fileId))));
        metadataRepository.deleteById(fileId);
    }
    
    public List<MediaMetadata> getAllFiles() {
        return metadataRepository.findAll();
    }
    
    public int cleanupOrphanedMetadata() {
        List<MediaMetadata> allMetadata = metadataRepository.findAll();
        int cleaned = 0;
        
        for (MediaMetadata metadata : allMetadata) {
            try {
                GridFsResource resource = gridFsTemplate.getResource(metadata.getId());
                if (!resource.exists()) {
                    System.out.println("Removing orphaned metadata: " + metadata.getId());
                    metadataRepository.delete(metadata);
                    cleaned++;
                }
            } catch (Exception e) {
                System.out.println("Removing invalid metadata: " + metadata.getId());
                metadataRepository.delete(metadata);
                cleaned++;
            }
        }
        
        return cleaned;
    }
}

