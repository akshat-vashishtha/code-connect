package com.codeconnect.curriculum.domain.repository;

import com.codeconnect.curriculum.domain.model.LessonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends MongoRepository<LessonDocument, String> {
    List<LessonDocument> findByModuleIdOrderBySequenceAsc(String moduleId);
    List<LessonDocument> findByTrackIdOrderBySequenceAsc(String trackId);
    Optional<LessonDocument> findByModuleIdAndSlug(String moduleId, String slug);
    long countByModuleId(String moduleId);
}
