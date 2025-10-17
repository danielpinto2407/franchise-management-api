package org.franchise.management.domain.repository;

import org.franchise.management.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ✅ Este puerto soportará los endpoints para agregar y modificar sucursales
 * dentro de una franquicia.
 */
public interface BranchRepository {

    Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch);

    Flux<Branch> findAllByFranchise(String franchiseId);

    Mono<Branch> findById(String franchiseId, String branchId);

    Mono<Branch> updateBranchName(String franchiseId, String branchId, String newName);

}
