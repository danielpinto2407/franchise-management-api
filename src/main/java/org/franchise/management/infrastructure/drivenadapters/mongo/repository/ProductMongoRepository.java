package org.franchise.management.infrastructure.drivenadapters.mongo.repository;

import org.franchise.management.domain.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 📦 Repositorio Mongo Reactivo para la entidad Product.
 * Permite realizar consultas personalizadas por branchId.
 */
@Repository
public interface ProductMongoRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findByBranchId(String branchId);

}