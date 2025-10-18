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
        log.info("✏️ Actualizando nombre de la sucursal '{}' a '{}'", branchId, newName);
        return branchRepository.updateBranchName(branchId, newName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada")))
                .onErrorResume(e -> {
                    log.error("❌ Error al actualizar nombre de sucursal: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
