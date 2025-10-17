package org.franchise.management.domain.repository;

import org.franchise.management.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ✅ Este repositorio permitirá implementar los casos de uso de creación,
 * consulta y actualización de franquicias.
 */
public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String franchiseId);

    Flux<Franchise> findAll();

    Mono<Franchise> update(Franchise franchise);

    Mono<Void> deleteById(String franchiseId);

}
