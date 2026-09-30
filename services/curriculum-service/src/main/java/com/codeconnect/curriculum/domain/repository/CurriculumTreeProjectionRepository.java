package com.codeconnect.curriculum.domain.repository;

import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the CQRS Read Model projection of curriculum hierarchy trees.
 */
@Repository
public interface CurriculumTreeProjectionRepository extends MongoRepository<CurriculumTreeProjectionDocument, String> {
}
