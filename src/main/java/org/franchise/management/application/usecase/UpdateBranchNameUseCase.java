package org.franchise.management.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> updateBranchName(String branchId, String newName) {
        return branchRepository.updateBranchName(branchId, newName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada")))
                .doOnNext(b -> log.info("✅ Sucursal actualizada: {} -> {}", b.getId(), b.getName()))
                .onErrorResume(e -> {
                    log.error("❌ Error actualizando sucursal: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
