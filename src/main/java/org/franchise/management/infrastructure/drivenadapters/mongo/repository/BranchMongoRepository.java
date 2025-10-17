package org.franchise.management.infrastructure.drivenadapters.mongo.repository;

import org.franchise.management.domain.model.Branch;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BranchMongoRepository extends ReactiveMongoRepository<Branch, String> {
}