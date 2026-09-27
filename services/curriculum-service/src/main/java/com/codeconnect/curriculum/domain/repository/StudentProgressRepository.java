package com.codeconnect.curriculum.domain.repository;

import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProgressRepository extends MongoRepository<StudentProgressDocument, String> {
    Optional<StudentProgressDocument> findByUserIdAndTrackId(String userId, String trackId);
}
