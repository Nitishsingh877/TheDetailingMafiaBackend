package com.theDetailingMafia.media_service.repository;


import com.theDetailingMafia.media_service.model.MediaMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaMetadataRepository extends MongoRepository<MediaMetadata, String> {
    List<MediaMetadata> findByEntityId(String entityId);
}
