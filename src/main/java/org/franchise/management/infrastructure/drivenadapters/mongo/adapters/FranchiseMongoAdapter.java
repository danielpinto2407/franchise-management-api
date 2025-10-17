package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import org.franchise.management.domain.model.Franchise;
import org.franchise.management.domain.repository.FranchiseRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.FranchiseMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
@RequiredArgsConstructor
public class FranchiseMongoAdapter implements FranchiseRepository {

    private final FranchiseMongoRepository franchiseMongoRepository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        log.info("💾 Guardando nueva franquicia: {}", franchise.getName());
        return franchiseMongoRepository.save(franchise);
    }

    @Override
    public Mono<Franchise> findById(String franchiseId) {
        return franchiseMongoRepository.findById(franchiseId);
    }

    @Override
    public Flux<Franchise> findAll() {
        return franchiseMongoRepository.findAll();
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        log.info("✏️ Actualizando franquicia: {}", franchise.getId());
        return franchiseMongoRepository.save(franchise);
    }

    @Override
    public Mono<Void> deleteById(String franchiseId) {
        return franchiseMongoRepository.deleteById(franchiseId);
    }
}