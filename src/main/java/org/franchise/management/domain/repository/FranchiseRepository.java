package org.franchise.management.domain.repository;

import org.franchise.management.domain.model.Franchise;
import reactor.core.publisher.Mono;

/**
 * ✅ Este repositorio permitirá implementar los casos de uso de creación
 * y actualización de franquicias.
 */
public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> update(Franchise franchise);

}
