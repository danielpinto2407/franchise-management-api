package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.BranchMongoRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.FranchiseMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
@RequiredArgsConstructor
public class BranchMongoAdapter implements BranchRepository {

    private final BranchMongoRepository branchMongoRepository;
    private final FranchiseMongoRepository franchiseMongoRepository;

    @Override
    public Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch) {
        return franchiseMongoRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada")))
                .flatMap(franchise -> {
                    branch.setFranchiseId(franchiseId);
                    return branchMongoRepository.save(branch)
                            .flatMap(savedBranch -> {
                                franchise.addBranch(savedBranch.getId());
                                log.info("Agregando sucursal '{}' a la franquicia '{}'", branch.getName(),
                                        franchiseId);
                                return franchiseMongoRepository.save(franchise)
                                        .thenReturn(savedBranch);
                            });
                });
    }

    @Override
    public Flux<Branch> findAllByFranchise(String franchiseId) {
        return franchiseMongoRepository.findById(franchiseId)
                .flatMapMany(franchise -> {
                    log.info("Listando sucursales de la franquicia '{}'", franchiseId);
                    return Flux.fromIterable(franchise.getBranchIds())
                            .flatMap(branchMongoRepository::findById);
                });
    }

    @Override
    public Mono<Branch> findById(String branchId) {
        return branchMongoRepository.findById(branchId);
    }

    @Override
    public Mono<Void> deleteBranchFromFranchise(String franchiseId, String branchId) {
        log.info("Eliminando sucursal '{}' de la franquicia '{}'", branchId, franchiseId);

        return franchiseMongoRepository.findById(franchiseId)
                .flatMap(franchise -> {
                    franchise.removeBranch(branchId);
                    return franchiseMongoRepository.save(franchise)
                            .then(branchMongoRepository.deleteById(branchId));
                });
    }

    @Override
    public Mono<Branch> updateBranchName(String branchId, String newName) {
        return branchMongoRepository.findById(branchId)
                .flatMap(branch -> {
                    branch.setName(newName);
                    return branchMongoRepository.save(branch);
                });
    }
}