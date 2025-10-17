package org.franchise.management.domain.repository;

import org.franchise.management.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ✅ Puerto del dominio para la gestión de sucursales.
 * Soporta operaciones reactivas con MongoDB.
 */
public interface BranchRepository {

    Mono<Branch> addBranchToFranchise(String franchiseId, Branch branch);

    Flux<Branch> findAllByFranchise(String franchiseId);

    Mono<Branch> findById(String branchId);

    Mono<Branch> updateBranchName(String branchId, String newName);

    Mono<Void> deleteBranchFromFranchise(String franchiseId, String branchId);
}
