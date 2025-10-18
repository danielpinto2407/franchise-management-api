package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Franchise;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.FranchiseMongoAdapter;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class CreateFranchiseUseCase {

    private final FranchiseMongoAdapter franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise)
                .doOnNext(f -> log.info("Nueva franquicia creada: " + f.getName()))
                .onErrorResume(e -> {
                    log.error("Error al crear franquicia: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
