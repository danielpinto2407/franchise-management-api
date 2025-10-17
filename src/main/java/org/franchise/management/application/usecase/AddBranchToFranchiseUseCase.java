package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Log4j2
public class AddBranchToFranchiseUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> addBranch(String franchiseId, Branch branch) {
        return branchRepository.addBranchToFranchise(franchiseId, branch)
                .doOnNext(b -> log.info("✅ Sucursal agregada: " + b.getName()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada.")))
                .onErrorResume(e -> {
                    log.error("❌ Error al agregar sucursal: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
