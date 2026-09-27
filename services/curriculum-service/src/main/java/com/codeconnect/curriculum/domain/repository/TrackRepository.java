package com.codeconnect.curriculum.domain.repository;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends MongoRepository<TrackDocument, String> {
    List<TrackDocument> findByStatus(TrackStatus status);
    Optional<TrackDocument> findBySlug(String slug);
}
