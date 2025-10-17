package org.franchise.management.domain.usecase;

import org.franchise.management.domain.model.Franchise;
import org.franchise.management.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Log4j2
public class CreateFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise)
                .doOnNext(f -> log.info("✅ Nueva franquicia creada: " + f.getName()))
                .onErrorResume(e -> {
                    log.error("❌ Error al crear franquicia: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
