package org.franchise.management.infrastructure.drivenadapters.mongo.repository;

import org.franchise.management.domain.model.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<Franchise, String> {
}