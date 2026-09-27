package com.codeconnect.curriculum.domain.repository;

import com.codeconnect.curriculum.domain.model.ModuleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends MongoRepository<ModuleDocument, String> {
    List<ModuleDocument> findByTrackIdOrderBySequenceAsc(String trackId);
    Optional<ModuleDocument> findByTrackIdAndSlug(String trackId, String slug);
}
