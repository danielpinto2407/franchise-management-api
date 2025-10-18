package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class AddBranchToFranchiseUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> addBranch(String franchiseId, Branch branch) {
        branch.setFranchiseId(franchiseId);
        return branchRepository.addBranchToFranchise(franchiseId, branch)
                .doOnNext(b -> log.info("✅ Sucursal agregada a franquicia {}: {}", franchiseId, b.getName()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada.")))
                .onErrorResume(e -> {
                    log.error("❌ Error al agregar sucursal: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
