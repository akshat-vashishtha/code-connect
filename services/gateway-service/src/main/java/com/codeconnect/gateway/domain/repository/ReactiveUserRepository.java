package com.codeconnect.gateway.domain.repository;

import com.codeconnect.gateway.domain.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Reactive MongoDB repository for User entity.
 */
@Repository
public interface ReactiveUserRepository extends ReactiveMongoRepository<User, String> {

    Mono<Boolean> existsByEmail(String email);

    Mono<User> findByEmail(String email);
}
