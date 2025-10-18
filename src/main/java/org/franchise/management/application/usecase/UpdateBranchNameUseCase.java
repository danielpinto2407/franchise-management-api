package org.franchise.management.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.BranchMongoAdapter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchMongoAdapter branchRepository;

    public Mono<Branch> updateBranchName(String branchId, String newName) {
        return branchRepository.updateBranchName(branchId, newName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada con id: " + branchId)));
    }

}
